package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.smartsteve.services.EarService;

@CommandDescription(value = "Turns off voice typing.")
public class TurnOffVoiceTypeCommand extends InstantCommand {

    public TurnOffVoiceTypeCommand(EarService eService) {
        setCommand(new Runnable() {
            @Override
            public void run() {
                eService.stopVoiceTyping();
            }
        });
    }
}
