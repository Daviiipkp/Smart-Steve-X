package com.daviipkp.smartsteve.services;

import com.daviipkp.smartsteve.Constants;
import com.daviipkp.smartsteve.model.ChatMessage;
import com.daviipkp.smartsteve.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DualBrainService {



    private final RestClient restClient;
    private final ChatRepository chatRepo;

    @Value("${google.api.key}")
    private String googleApiKey;

    public DualBrainService(ChatRepository arg0) {
        this.restClient = RestClient.create();
        this.chatRepo = arg0;
    }

    public String processCommand(String userPrompt) {
        String context = getContext();

        String localResponse = callOllamaLocal(userPrompt);
        String cloudResponse = callGeminiCloud(userPrompt, localResponse, context);

        if(Constants.DEBUG) {
            System.out.println("============== NOVA REQUISIÇÃO ==============");
            System.out.println("Usuario: " + userPrompt);
            System.out.println(">> Local: " + localResponse);
            System.out.println(">> Nuvem: " + cloudResponse);
        }

        ChatMessage chatMessage = new ChatMessage(userPrompt, localResponse + cloudResponse);
        //chatRepo.save(chatMessage);
        if(Constants.DEBUG) {
            System.out.println(">> Memória salva no banco H2.");
        }

        return localResponse + " " + cloudResponse;
    }

    private String getContext() {
        List<ChatMessage> messages = chatRepo.findTop10ByOrderByTimestampDesc();

        Collections.reverse(messages);

        if(messages.isEmpty()) {
            return "No context";
        }

        return messages.stream()
                .map(m -> "User: " + m.getUserPrompt() + "\nSteve: " + m.getSteveResponse())
                .collect(Collectors.joining("\n---\n"));
    }

    private String callOllamaLocal(String userPrompt) {
        try {
            long l = System.currentTimeMillis();
            String url = "http://localhost:11434/api/generate";

            var body = Map.of(
                    "model", "deepseek-r1:8b",
                    "prompt", Constants.LOCAL_PROMPT + userPrompt,
                    "stream", false
            );

            String jsonResponse = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
            System.out.println((System.currentTimeMillis() - l) + "ms delay");
            return getOllamaTextFromJson(jsonResponse);
        }catch(Exception e) {e.printStackTrace();return "";}

    }

    private String callGeminiCloud(String userPrompt, String localResponse, String context) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" + googleApiKey;
            String finalPrompt = String.format(Constants.REMOTE_PROMPT, userPrompt, localResponse,  context);
            var body = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", finalPrompt)
                            ))
                    )
            );

            String jsonResponse = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
            return getGeminiTextFromJson(jsonResponse);
        }catch(Exception e) {e.printStackTrace();return "";}
    }

    private String getGeminiTextFromJson(String json) {
        if (json == null) return "";

        // Procura onde começa o texto
        String marcador = "\"text\": \"";
        int indexMarcador = json.lastIndexOf(marcador);

        // SE NÃO ACHAR TEXTO (O Gemini decidiu ficar calado):
        if (indexMarcador == -1) {
            return ""; // Retorna vazio, tudo certo.
        }

        // Se achar, faz o corte normal
        int start = indexMarcador + marcador.length();
        int end = json.indexOf("\"", start);

        return json.substring(start, end).replace("\\n", " ");
    }

    private String getOllamaTextFromJson(String json) {
        if (json == null) return "";
        int start = json.indexOf("\"response\":\"") + 12;
        int end = json.indexOf("\",\"done\"");
        if (start < 12 || end == -1) return "Error parser Ollama: " + json;
        return json.substring(start, end).replace("\\n", " ");
    }

}

