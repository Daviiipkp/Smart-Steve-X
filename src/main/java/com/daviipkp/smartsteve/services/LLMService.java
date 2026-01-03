package com.daviipkp.smartsteve.services;

import com.daviipkp.smartsteve.Constants;
import com.daviipkp.smartsteve.Instance.Command;
import com.daviipkp.smartsteve.Utils;
import com.daviipkp.smartsteve.Instance.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LLMService {

    private static final String defaultModel = "https://ai.hackclub.com/proxy/v1/chat/completions";
    static String apiKey;

    @Value("${hcai.api.key}")
    public void setApiKey(String value) {
        apiKey = value;
    }

    @Autowired
    private CommandRegistry cmdRegistry;

    private ChatMessage finalCallModel(String fullPromptText, String userPrompt) {
        String escapedPrompt = fullPromptText
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");

        String jsonBody = """
        {
            "model": "google/gemini-3-flash-preview",
            "messages": [
                {
                    "role": "user", 
                    "content": "%s"
                }
            ]
        }
        """.formatted(escapedPrompt);

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(defaultModel))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("error: " + response.statusCode());
                System.err.println("error bodyy: " + response.body());
                return null;
            }
            ChatMessage s = ChatMessage.fromJson(response.body(), userPrompt);
            String action = s.getCommand();
            if (action != null && !action.equals("null") && !action.isEmpty()) {

                for(Command cs : cmdRegistry.getCommands()) {
                    if(cs.getID().contains((action.contains("___")?action.split("___")[0]:action))) {
                        try {
                            String[] sa = new String[2];
                            sa[0] = action.split("___")[1];
                            sa[1] = s.getContext();
                            cs.setArguments(sa).execute();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            cmdRegistry.handleNotFound(action);
                            return null;
                        }
                    }
                }

            }
            return s;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ChatMessage callModel(String userPrompt) {
        String fullPromptText = String.format(Constants.getPrompt(true, false, false), cmdRegistry.getCommandNamesWithDesc())
                + "\n" + userPrompt;
        return finalCallModel(fullPromptText, userPrompt);
    }

    public ChatMessage callInstructedModel(String userPrompt, String sysInstructions, boolean sendCommands) {
        String fullPromptText;
        if(sendCommands) {
            fullPromptText = String.format(Constants.getPrompt(sendCommands, false, true), cmdRegistry.getCommandNamesWithDesc(), sysInstructions)
                    + "\n" + userPrompt;
        }else{
            fullPromptText = String.format(Constants.getPrompt(sendCommands, false, true), sysInstructions)
                    + "\n" + userPrompt;
        }
        return finalCallModel(fullPromptText, userPrompt);
    }

    public  ChatMessage callContextedModel(String userPrompt, String context) {
        String fullPromptText = String.format(Constants.getPrompt(true, true, false), cmdRegistry.getCommandNamesWithDesc(), context)
                + "\n" + userPrompt;
        return finalCallModel(fullPromptText, userPrompt);
    }

    public ChatMessage callModel(String userPrompt, String context, String sysInstructions) {
        String fullPromptText = String.format(Constants.getPrompt(true, true, true), cmdRegistry.getCommandNamesWithDesc(), context, sysInstructions)
                + "\n" + userPrompt;
        return finalCallModel(fullPromptText, userPrompt);
    }


}
