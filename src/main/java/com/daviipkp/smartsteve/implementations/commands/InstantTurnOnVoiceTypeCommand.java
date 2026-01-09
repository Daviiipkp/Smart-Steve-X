package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.smartsteve.services.EarService;
import org.springframework.stereotype.Component;

@Component
public class InstantTurnOnVoiceTypeCommand extends InstantCommand {
    public InstantTurnOnVoiceTypeCommand(EarService eService) {
        setCommand(new Runnable() {
            @Override
            public void run() {
                eService.startVoiceTyping();
            }
        });
    }
}
