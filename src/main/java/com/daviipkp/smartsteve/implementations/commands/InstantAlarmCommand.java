package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.smartsteve.services.EarService;
import com.daviipkp.smartsteve.services.SpringContext;
import com.daviipkp.smartsteve.services.VoiceService;

import java.io.File;
import java.io.IOException;

@CommandDescription(value = "Use to play the Alarm instantly.",
        possibleArguments = "",
        exampleUsage = "")
public class InstantAlarmCommand extends InstantCommand {

    public InstantAlarmCommand() {
        setCommand(() -> {

            File m = new File("D:\\Coding\\Projects\\smartsteve\\alarm.mp3");
            try {
                Runtime.getRuntime().exec("cmd /c start \"\" \"" + m.getAbsolutePath() + "\"");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
