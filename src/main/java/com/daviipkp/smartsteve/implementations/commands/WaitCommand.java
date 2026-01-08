package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.Command;
import com.daviipkp.SteveCommandLib.instance.QueuedCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;

@CommandDescription(value = "Forces the Command Queue to wait before executing another command",
        possibleArguments = "time: <number in seconds>",
        exampleUsage = "time: 300")
public class WaitCommand extends QueuedCommand {

    private long counter = 0;

    @Override
    public void handleError(Exception e) {

    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public void execute(long delta) {
        this.counter += delta;
        if(this.counter >= Long.parseLong(getArgument("time")) * 1000) {
            finish();
        }
        super.execute(delta);
    }

}
