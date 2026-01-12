package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;

import java.io.IOException;

@CommandDescription(value = "Command designed to shutdown the computer/system.")
public class SystemShutdownCommand extends InstantCommand {

    @Describe(description = "<Time in seconds>")
    private String time;

    public SystemShutdownCommand() {
        setCommand(new Runnable() {
            public void run() {
                ProcessBuilder pb = new ProcessBuilder("shutdown", "/s", "/f", "/t", time);
                try {
                    pb.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void handleError(Exception e) {
    }
}
