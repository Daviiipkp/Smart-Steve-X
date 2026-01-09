package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;

@CommandDescription(value = "Activates Inspection Mode.", possibleArguments = "")
public class InstantTurnOnInspectionModeCommand extends InstantCommand {
    public InstantTurnOnInspectionModeCommand() {
        setCommand(new Runnable() {
            @Override
            public void run() {
                SteveCommandLib.debug(true);
            }
        });
    }
}
