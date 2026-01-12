package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;
import com.daviipkp.smartsteve.services.EarService;
import com.daviipkp.smartsteve.services.SpringContext;
import com.daviipkp.smartsteve.services.VoiceService;

@CommandDescription(value = "Use to talk anything that you want. Any message as argument of this command will be spoke directly to the user.",
        exampleUsage = "message: Hello, sir")
public class TalkCommand extends InstantCommand {

    @Describe
    private String message;

    public TalkCommand() {
        setCommand(new Runnable() {
            @Override
            public void run() {
                EarService service = SpringContext.getBean(EarService.class);
                service.stopListening();
                VoiceService.speak(message, service::resumeListening);
            }
        });
    }
}
