package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.Command;
import com.daviipkp.SteveCommandLib.instance.QueuedCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;
import com.daviipkp.smartsteve.services.LLMService;
import com.daviipkp.smartsteve.services.SpringContext;
import lombok.Builder;
import lombok.Getter;

@Builder
@CommandDescription(value = "Use when you need a callback after other commands. If you want to be called for another function after some time, use this after a WaitCommand, but remember to tell the next Callback model to not repeat what you've said, just confirm the order.",
        exampleUsage = "system_instructions: User asked me to turn on TV. I turned it on. Send a success message.")
public class CallbackCommand extends QueuedCommand {

    @Describe
    private String user_prompt;
    @Describe
    private String system_instructions;

    @Override
    public void handleError(Exception e) {

    }

    @Override
    public void execute(long delta) {
        super.execute(delta);
        LLMService llm = SpringContext.getBean(LLMService.class);
        llm.callDefInstructedModel(user_prompt, system_instructions, true);
        finish();
    }

    public static void asError(Class<? extends Command> command, Exception exception) throws InstantiationException, IllegalAccessException {
        SteveCommandLib.addCommand(CallbackCommand.builder().system_instructions("User asked for " + command.getSimpleName() + " but it failed with exception " + exception.getClass().getSimpleName() + " and message " + exception.getMessage()).build());
    }

}
