package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.smartsteve.Instance.CommandE;
import org.springframework.stereotype.Component;

public class KillSwitchCommand extends InstantCommand {

    public KillSwitchCommand() {
        super(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
