package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.smartsteve.services.EarService;
import org.springframework.stereotype.Component;

@CommandDescription(value = "Turns on voice typing.")
public class TurnOnVoiceTypeCommand extends InstantCommand {
    public TurnOnVoiceTypeCommand(EarService eService) {
        setCommand(new Runnable() {
            @Override
            public void run() {
                eService.startVoiceTyping();
            }
        });
    }
}
