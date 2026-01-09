package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;

import java.io.IOException;

@CommandDescription(value = "Command designed to shutdown the computer/system. Argument is time in seconds for it to shutdown. No argument means instantly.",
        possibleArguments = "time: <number in seconds>",
        exampleUsage = "time: 300")
public class InstantSystemShutdownCommand extends InstantCommand {

    public InstantSystemShutdownCommand() {
        setCommand(new Runnable() {
            public void run() {
                ProcessBuilder pb = new ProcessBuilder("shutdown", "/s", "/f", "/t", getArgument("time"));
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
