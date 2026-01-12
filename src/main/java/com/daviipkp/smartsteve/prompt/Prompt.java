package com.daviipkp.smartsteve.prompt;

import com.daviipkp.smartsteve.Utils;
import com.daviipkp.smartsteve.services.DualBrainService;
import com.daviipkp.smartsteve.services.LLMService;
import com.daviipkp.smartsteve.services.SpringContext;
import lombok.Getter;

@Getter
public class Prompt {

    private static final DualBrainService dbs = SpringContext.getBean(DualBrainService.class);

    public static String getDefaultPrompt(String userPrompt) {
        return createPrompt(system_role, system_rules, output_format, getContext(), getMemoryConsultation(userPrompt), getUserPrompt(userPrompt));
    }

    public static String getStartupPrompt() {
        return createPrompt(system_role, system_rules, output_format, first_boot);
    }

    public static String createPrompt(PromptComponent... components) {
        StringBuilder sb = new StringBuilder();
        for (PromptComponent component : components) {
            sb.append("\n");
            sb.append("### ").append(component.getHeader().toUpperCase());
            sb.append(component.getContent());
            sb.append("\n");
            sb.append(component.getFooter());
        }
        return sb.toString();
    }

    public static String addComponents(String prompt, PromptComponent... components) {
        StringBuilder sb = new StringBuilder();
        sb.append(prompt);
        for (PromptComponent component : components) {
            sb.append("\n");
            sb.append("### ").append(component.getHeader().toUpperCase());
            sb.append(component.getContent());
            sb.append("\n");
            sb.append(component.getFooter());
        }
        return sb.toString();
    }



    private static PromptComponent getMemoryConsultation(String userPrompt) {
        return PromptComponent.builder().header("memory consultation").content(dbs.getMemoryConsult(userPrompt)).build();
    }

    private static PromptComponent getContext() {
        return PromptComponent.builder().header("prompt context")
                .content(dbs.getContext()).build();
    }

    private static PromptComponent getSystemInstructions(String instructions) {
        return PromptComponent.builder().header("system instructions")
                .content(instructions).build();
    }

    private static PromptComponent getUserPrompt(String userPrompt) {
        return PromptComponent.builder().header("user prompt")
                .content(userPrompt).build();
    }

    private static final PromptComponent system_role = PromptComponent.builder().header("system role")
                .content("""
                        You are Steve, an ultra-efficient assistant.
                        ALWAYS answer the user with any of the talk commands.
                        ALWAYS respect the Json mandatory system.
                        """).build();

    private static final PromptComponent output_format = PromptComponent.builder().header("output format - mandatory")
            .content("""
                        You must ONLY return a raw JSON object. No markdown, no preambles. Follow THIS structure:
                        {
                          "status": "SUCCESS", "DOING", "IGNORE",
                          "action": {
                            "COMMAND_ID": {
                              "argument": "value"
                            }
                          },
                          "memory": "Concise log of what just happened (User intent + Your Action) to serve as context for the NEXT turn."
                        }
                        
                        EXAMPLE Structure:
                        {
                          "status": "SUCCESS",
                          "action": {
                            "SystemShutdownCommand": {
                              "time": "20"
                            },
                            "InstantTalkCommand": {
                                "message": "Yes, sir. Shutting down in 20."
                            }
                          },
                          "memory": "User asked me to shutdown the system in 20 seconds. I sent the command to do it."
                        }
                        """).build();

    private static final PromptComponent system_rules = PromptComponent.builder().header("system rules")
            .content("""
                        1. Persona: Address user as "Sir". English Only.
                        2. Ultra-Brevity: For successful command executions, the preferred speech is simply "Yes, sir." (Implies: "Done").
                        3. Strict Command Matching: NEVER invent commands. Check the AVAILABLE COMMANDS list.
                        4. Refusal vs Help: If the user asks for an action NOT in the list, refuse it.
                        5. Vague Inputs: If input is meaningless (e.g., "huh", "but", "a"), set "status": "IGNORE", "speech": null, and "memory": null.
                        """).build();

    private static final PromptComponent command_list = PromptComponent.builder().header("list of available commands")
            .content(Utils.getCommandNamesWithDesc()).build();

    private static final PromptComponent first_boot = PromptComponent.builder().header("first boot system instructions")
            .content("""
                This is the first boot call, which means system was just started.
                This system instruction is generated automatically.
                """).build();

}
