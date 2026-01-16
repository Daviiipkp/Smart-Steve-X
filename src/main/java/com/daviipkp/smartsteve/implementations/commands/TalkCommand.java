package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;
import com.daviipkp.smartsteve.services.EarService;
import com.daviipkp.smartsteve.services.SpringContext;
import com.daviipkp.smartsteve.services.VoiceService;

@CommandDescription(value = "Use to talk anything that you want. Any message as argument of this command will be spoke directly to the user. Volume range is 0 to 100, where 100 is a scream and 0 is inaudible. 30 is normal voice.")
public class TalkCommand extends InstantCommand {

    @Describe
    private String message;

    @Describe
    private float volume;

    public TalkCommand() {
        setCommand(new Runnable() {
            @Override
            public void run() {
                VoiceService.setVolume((volume/100));
                VoiceService.speak(message);
            }
        });
    }
}
