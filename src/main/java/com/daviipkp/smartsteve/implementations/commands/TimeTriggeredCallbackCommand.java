package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.TriggeredCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;

import java.time.LocalDateTime;

@CommandDescription(value = "Used to have a callback when a certain time comes. Useful if the user asks a Instant Command to happen in a specific time.",
        exampleUsage = "time: 1/1/2026 3:00PM")
public class TimeTriggeredCallbackCommand extends TriggeredCommand {

    @Describe(description = "<Formatted Date/Time>")
    private String time;

    private final LocalDateTime triggerTime;

    public TimeTriggeredCallbackCommand() {
        triggerTime = LocalDateTime.parse(time);
    }

    @Override
    public boolean checkTrigger() {
        return triggerTime.isAfter(LocalDateTime.now());
    }

    @Override
    public void handleError(Exception e) {

    }

    @Override
    public void execute(long delta) {

    }


}
