package com.daviipkp.smartsteve.services;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveJsoning.SteveJsoning;
import com.daviipkp.smartsteve.Constants;
import com.daviipkp.smartsteve.Instance.ChatMessage;
import com.daviipkp.smartsteve.repository.ChatRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class DualBrainService {



    private final RestClient restClient;
    private final ChatRepository chatRepo;

    private final VoiceService voiceService;
    private final EarService earService;
    private final SearchService searchService;
    private final LLMService llmS;
    @Getter
    @Setter
    private static boolean voiceTyping = false;

    @Value("${google.api.key}")
    private String googleApiKey;

    public DualBrainService(ChatRepository arg0, VoiceService voiceService, @Lazy EarService earService, LLMService llmservice, SearchService searchService) {
        this.searchService = searchService;
        this.restClient = RestClient.create();
        this.chatRepo = arg0;
        this.earService = earService;
        this.voiceService = voiceService;
        this.llmS = llmservice;
        SteveCommandLib.debug(true);
    }

    public String processCommand(String userPrompt) throws ExecutionException, InterruptedException {
        String context = getContext();

        CompletableFuture<ChatMessage> fResponse = CompletableFuture.supplyAsync(() -> {
            return llmS.callDefContextedModel(userPrompt, context);
        });

        CompletableFuture.allOf(fResponse).join();


        ChatMessage cResponse = fResponse.get();

        chatRepo.save(cResponse);
        if(Constants.DATABASE_SAVING_DEBUG) {
            SteveCommandLib.systemPrint(">> Memória salva no banco H2: " + cResponse);
        }

        return cResponse.getSteveResponse();
    }

    private String getContext() {
        List<ChatMessage> messages = chatRepo.findTop3ByOrderByTimestampDesc();

        Collections.reverse(messages);

        if(messages.isEmpty()) {
            return "No context";
        }

        return messages.stream()
                .map(m -> {
                    try {
                        return "User: " + m.getUserPrompt()
                                + "\nSteve memory: " + SteveJsoning.valueAtPath("/memory", m.getSteveResponse())
                                + "\nTimestamp: " + m.getTimestamp();
                    } catch (JsonProcessingException e) {
                        return "";
                    }
                })
                .collect(Collectors.joining("\n---\n"));
    }

}

