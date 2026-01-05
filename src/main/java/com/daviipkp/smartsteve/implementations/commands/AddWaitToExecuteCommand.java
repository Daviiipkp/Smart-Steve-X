package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.smartsteve.Instance.CommandE;
import org.springframework.stereotype.Component;

@Component
public class AddWaitToExecuteCommand extends CommandE {
    @Override
    public void execute() {
        try {
            Thread.sleep(Long.parseLong(getArguments()[0])*1000);
        }catch (Exception e){
            e.printStackTrace();
        }
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
        return "Makes the computer wait to execute the next queued command. Can be used with other commands only. Example usage: " + this.getID() + "___10&&SystemShutdownCommand";
    }
}
