package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.smartsteve.services.EarService;

public class InstantTurnOffVoiceTypeCommand extends InstantCommand {

    public InstantTurnOffVoiceTypeCommand(EarService eService) {
        setCommand(new Runnable() {
            @Override
            public void run() {
                eService.stopVoiceTyping();
            }
        });
    }
}
