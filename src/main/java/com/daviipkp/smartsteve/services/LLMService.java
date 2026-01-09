package com.daviipkp.smartsteve.services;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.Command;
import com.daviipkp.SteveJsoning.SteveJsoning;
import com.daviipkp.smartsteve.Constants;
import com.daviipkp.smartsteve.Instance.ChatMessage;
import com.daviipkp.smartsteve.Instance.SteveResponse;
import com.daviipkp.smartsteve.Utils;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class LLMService {

    private static final String defaultProvider = "https://ai.hackclub.com/proxy/v1/chat/completions";
    private static final String defaultModel = "google/gemini-3-flash-preview";
    static String apiKey;

    private long lastTick = 0;

    @Value("${hcai.api.key}")
    public void setApiKey(String value) {
        apiKey = value;
    }

    @Scheduled(fixedRate = 50)
    private void tick() {
        if(lastTick == 0) {
            lastTick = System.currentTimeMillis();
        }
        SteveCommandLib.tick(System.currentTimeMillis() - lastTick);
        lastTick = System.currentTimeMillis();
    }

    private ChatMessage finalCallModel(String fullPromptText, String userPrompt) {
        String escapedPrompt = fullPromptText
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");

        String jsonBody = """
        {
            "model": "%s",
            "messages": [
                {
                    "role": "user", 
                    "content": "%s"
                }
            ]
        }
        """.formatted(defaultModel, escapedPrompt);

        try {
            long time = System.currentTimeMillis();
            if(Constants.FINAL_PROMPT_DEBUG) {
                SteveCommandLib.systemPrint("Prompt sent: " + fullPromptText);
            }
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(defaultProvider))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            SteveCommandLib.systemPrint("Request time: " + (System.currentTimeMillis() - time));

            if (response.statusCode() != 200) {
                System.err.println("error: " + response.statusCode());
                System.err.println("error bodyy: " + response.body());
                return null;
            }


            SteveResponse s = SteveJsoning.parse(SteveJsoning.valueAtPath("/choices/0/message/content", response.body()), SteveResponse.class);

            Set<Class<?>> registeredCmds = Utils.getRegisteredCommands();
            for(String cmd : s.action.keySet()) {
                try {
                    Command command = Utils.getCommandByName(cmd);
                    for(String argName : s.action.get(cmd).keySet()) {
                        command.setArgument(argName,  s.action.get(cmd).get(argName));
                    }
                    SteveCommandLib.addCommand(command);
                } catch (Exception e) {
                    System.out.println("Could find command or it needs a constructor parameter.");
                }
            }


            return new ChatMessage(userPrompt, SteveJsoning.valueAtPath("/choices/0/message/content", response.body()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ChatMessage callDefModel(String userPrompt) {
        String fullPromptText = String.format(Constants.getDefaultPrompt(true, false, false), Utils.getCommandNamesWithDesc())
                + "\n" + userPrompt;
        return finalCallModel(fullPromptText, userPrompt);
    }

    public ChatMessage callDefInstructedModel(String userPrompt, String sysInstructions, boolean sendCommands) {
        String fullPromptText;
        if(sendCommands) {
            fullPromptText = String.format(Constants.getDefaultPrompt(sendCommands, false, true), Utils.getCommandNamesWithDesc(), sysInstructions)
                    + "\n" + userPrompt;
        }else{
            fullPromptText = String.format(Constants.getDefaultPrompt(sendCommands, false, true), sysInstructions)
                    + "\n" + userPrompt;
        }
        return finalCallModel(fullPromptText, userPrompt);
    }

    public  ChatMessage callDefContextedModel(String userPrompt, String context) {
        String fullPromptText = String.format(Constants.getDefaultPrompt(true, true, false, true), Utils.getCommandNamesWithDesc(), getMemoryConsult(userPrompt), context)
                + "\n" + userPrompt;
        return finalCallModel(fullPromptText, userPrompt);
    }

    public ChatMessage callDefModel(String userPrompt, String context, String sysInstructions) {
        String fullPromptText = String.format(Constants.getDefaultPrompt(true, true, true), Utils.getCommandNamesWithDesc(), context, sysInstructions)
                + "\n" + userPrompt;
        return finalCallModel(fullPromptText, userPrompt);
    }

    public String getMemoryConsult(String query) {
        VectorStore vectorStore = SpringContext.getBean(VectorStore.class);

        SearchRequest request = SearchRequest.query(query)
                .withTopK(5)
                .withSimilarityThreshold(0.5);

        List<Document> r = vectorStore.similaritySearch(request);

        StringBuilder sb = new StringBuilder();
        if(Constants.MEMORY_DEBUG) {
            SteveCommandLib.systemPrint("Trying to get Memory Consult");
        }

        for (Document doc : r) {
            String c = doc.getContent();
            Map<String, Object> m = doc.getMetadata();
            sb.append("- ").append(c).append("\n");
            if(Constants.MEMORY_DEBUG) {
                SteveCommandLib.systemPrint("Memory added: " + c);
            }
        }


        return sb.toString();

    }

}
