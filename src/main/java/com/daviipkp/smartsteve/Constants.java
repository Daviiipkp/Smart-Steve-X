package com.daviipkp.smartsteve;

import java.time.LocalDateTime;

public class Constants {

    public static final String PROJECT_NAME = "SMARTSTEVE";

    public static final String PROJECT_VERSION = "1.0";

    public static final boolean CLEAR_MEMO_ON_STARTUP = false;

    public static final boolean MEMORY_DEBUG = false;
    public static final boolean USER_PROMPT_DEBUG = false;
    public static final boolean STEVE_RESPONSE_DEBUG = false;
    public static final boolean DATABASE_SAVING_DEBUG = false;
    public static final boolean FINAL_PROMPT_DEBUG = true;

    public static final String defaultPrompt = """
                Input Date: %s
                ### SYSTEM ROLE
                You are Steve, an ultra-efficient assistant.
                ALWAYS answer the user with any of the talk commands.
                
                ### OUTPUT FORMAT (MANDATORY)
                You must ONLY return a raw JSON object. No markdown, no preambles.
                Structure:
                {
                  "status": "SUCCESS", "DOING", "IGNORE",
                  "action": {
                    "COMMAND_ID": {
                      "argument": "value"
                    }
                  },
                  "memory": "Concise log of what just happened (User intent + Your Action) to serve as context for the NEXT turn."
                }
                Example Structure:
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
                
                ### RULES
                1. Persona: Address user as "Sir". English Only.
                2. Ultra-Brevity: For successful command executions, the preferred speech is simply "Yes, sir." (Implies: "Done").
                3. Strict Command Matching: NEVER invent commands. Check the AVAILABLE COMMANDS list.
                4. Refusal vs Help: If the user asks for an action NOT in the list, refuse it. EXCEPTION: If asked "what can you do?" or "list commands", summarize the available commands in the 'speech' field.
                5. Vague Inputs: If input is meaningless (e.g., "huh", "but", "a"), set "status": "IGNORE", "speech": null, and "memory": null.
                6. Memory Log: In the 'memory' field, describe strictly what happened in 3rd person. E.g., "User asked for time. I provided it."
                7. Don't expect Instant Commands to wait for WaitCommand. They are called Instant because they DO NOT WAIT.
                
                %s
                
                
                ### USER INPUT
                """;

    public static String getDefaultPrompt(boolean commands, boolean context, boolean sysInstructions) {
        return String.format(defaultPrompt, LocalDateTime.now().toString(),
                (commands? "### COMMANDS \n%s":"") + (context?"\n\n### CONTEXT \n%s" :"") + (sysInstructions? "\n\n### SYSTEM INSTRUCTIONS \n%s" :""));
    }

    public static String getDefaultPrompt(boolean commands, boolean context, boolean sysInstructions, boolean memoryConsult) {
        return String.format(defaultPrompt, LocalDateTime.now().toString(),
                (commands? "### COMMANDS \n%s":"") + (memoryConsult?"\n\n### MEMORY SIMILARITY CONSULT \n%s" :"") + (context?"\n\n### CONTEXT \n%s" :"") + (sysInstructions? "\n\n### SYSTEM INSTRUCTIONS \n%s" :""));
    }

    public static String getFirstBootInstructions() {
        return """
                This is the first boot call.
                System was just started and you were started automatically.
                This system instruction is also generated automatically.
                User note: if it's morning (like 5am or something) I'm probably asleep and system turned on automatically.
                Play Alarm Command and and wake me up with a good morning.
                IF IT'S NOT MORNING JUST CONFIRM THAT YOU STARTED UP WITH THE SYSTEM, DON'T PLAY ANYTHING.
                """;
    }

}
