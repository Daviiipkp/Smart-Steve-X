package com.daviipkp.smartsteve.prompt;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromptComponent {

    private String header = "";
    private String content = "";
    private String footer = "";

}
