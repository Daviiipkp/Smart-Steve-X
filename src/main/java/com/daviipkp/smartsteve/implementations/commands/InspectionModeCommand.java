package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.smartsteve.Instance.CommandE;
import org.springframework.stereotype.Component;

@Component
public class InspectionModeCommand extends CommandE {
    @Override
    public void execute() {

    }

    @Override
    public void callback() {

    }

    @Override
    public void executeSupCallback() {

    }

    @Override
    public String getID() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getDescription() {
        return "";
    }
}
