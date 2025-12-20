package com.daviipkp.smartsteve.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;

    @Lob
    private String userPrompt;

    @Lob
    private String steveResponse;

    public ChatMessage(String userPrompt, String steveResponse) {
        this.userPrompt = userPrompt;
        this.steveResponse = steveResponse;
        this.timestamp = LocalDateTime.now();
    }


}
