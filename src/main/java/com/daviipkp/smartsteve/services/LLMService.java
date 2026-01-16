package com.daviipkp.smartsteve.services;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.Command;
import com.daviipkp.SteveJsoning.SteveJsoning;
import com.daviipkp.smartsteve.Constants;
import com.daviipkp.smartsteve.Instance.ChatMessage;
import com.daviipkp.smartsteve.Instance.Protocol;
import com.daviipkp.smartsteve.Instance.SteveResponse;
import com.daviipkp.smartsteve.Utils;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
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

    public ChatMessage finalCallModel(String fullPromptText, String userPrompt) {
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
            if(s.action != null) {
                for(String cmd : s.action.keySet()) {
                    try {
                        Command command = Utils.getCommandByName(cmd);
                        for(String argName : s.action.get(cmd).keySet()) {
                            try {
                                Field field = command.getClass().getDeclaredField(argName);
                                field.setAccessible(true);
                                Object obj = s.action.get(cmd).get(argName);
                                if(field.getType().isAssignableFrom(String.class)) {
                                    field.set(command, s.action.get(cmd).get(argName));
                                }else if(field.getType().isAssignableFrom(int.class)) {
                                    field.set(command, Integer.parseInt(s.action.get(cmd).get(argName)));
                                }else if(field.getType().isAssignableFrom(float.class)) {
                                    field.set(command, Float.parseFloat(s.action.get(cmd).get(argName)));
                                }else if(field.getType().isAssignableFrom(boolean.class)) {
                                    field.set(command, Boolean.parseBoolean(s.action.get(cmd).get(argName)));
                                }else if(field.getType().isAssignableFrom(long.class)) {
                                    field.set(command, Long.parseLong(s.action.get(cmd).get(argName)));
                                }
                            } catch (NoSuchFieldException e) {
                            }
                        }
                        SteveCommandLib.addCommand(command);
                    } catch (Exception e) {
                        System.out.println("Couldn't find command or it needs a constructor parameter. Exception: " + e.getMessage() );
                    }
                }
            }


            return new ChatMessage(userPrompt, SteveJsoning.valueAtPath("/choices/0/message/content", response.body()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void finalCallModel(String fullPromptText) {
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
                return;
            }


            SteveResponse s = SteveJsoning.parse(SteveJsoning.valueAtPath("/choices/0/message/content", response.body()), SteveResponse.class);
            if(Constants.STEVE_RESPONSE_DEBUG) {
                SteveCommandLib.systemPrint("STEVE RESPONSE: " + SteveJsoning.valueAtPath("/choices/0/message/content", response.body()));
            }
            for(String cmd : s.action.keySet()) {
                try {
                    Command command = Utils.getCommandByName(cmd);
                    for(String argName : s.action.get(cmd).keySet()) {
                        try {
                            Field field = command.getClass().getDeclaredField(argName);
                            field.setAccessible(true);
                            Object obj = s.action.get(cmd).get(argName);
                            if(field.getType().isAssignableFrom(String.class)) {
                                field.set(command, s.action.get(cmd).get(argName));
                            }else if(field.getType().isAssignableFrom(int.class)) {
                                field.set(command, Integer.parseInt(s.action.get(cmd).get(argName)));
                            }else if(field.getType().isAssignableFrom(float.class)) {
                                field.set(command, Float.parseFloat(s.action.get(cmd).get(argName)));
                            }else if(field.getType().isAssignableFrom(boolean.class)) {
                                field.set(command, Boolean.parseBoolean(s.action.get(cmd).get(argName)));
                            }
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    }
                    SteveCommandLib.addCommand(command);
                } catch (Exception e) {
                    System.out.println("Couldn't find command or it needs a constructor parameter. Exception: " );
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
