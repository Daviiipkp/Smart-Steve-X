package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.QueuedCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.smartsteve.services.LLMService;
import com.daviipkp.smartsteve.services.SpringContext;

@CommandDescription(value = "This command goes to the queue. Use when you need a callback after other commands. If you want to be called for another function after some time, use this after a WaitCommand, but remember to tell the next Callback model to not repeat what you've said, just confirm the order.",
        possibleArguments = {
        "user_prompt: <String>",
        "system_instructions: <String>"},
        exampleUsage = "system_instructions: User asked me to turn on TV. I turned it on. Send a success message.")
public class QueuedCallbackCommand extends QueuedCommand {
    @Override
    public void handleError(Exception e) {

    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public void execute(long delta) {
        super.execute(delta);
        LLMService llm = SpringContext.getBean(LLMService.class);
        llm.callDefInstructedModel(getArgument("user_prompt"), getArgument("system_instructions"), true);
        finish();
    }
}
