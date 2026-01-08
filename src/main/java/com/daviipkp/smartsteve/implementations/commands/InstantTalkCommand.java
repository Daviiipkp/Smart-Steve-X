package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.smartsteve.services.EarService;
import com.daviipkp.smartsteve.services.SpringContext;
import com.daviipkp.smartsteve.services.VoiceService;

@CommandDescription(value = "Use to talk anything that you want. Any message as argument of this command will be spoke directly to the user.",
        possibleArguments = "message: <String>",
        exampleUsage = "message: Hello, sir")
public class InstantTalkCommand extends InstantCommand {

    public InstantTalkCommand() {
        setCommand(new Runnable() {
            @Override
            public void run() {
                EarService service = SpringContext.getBean(EarService.class);
                service.stopListening();
                VoiceService.speak(getArgument("message"), service::resumeListening);
            }
        });
    }
}
