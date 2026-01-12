package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;

import java.io.File;
import java.io.IOException;

@CommandDescription(value = "Use to play the Alarm.")
public class AlarmCommand extends InstantCommand {

    public AlarmCommand() {
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
