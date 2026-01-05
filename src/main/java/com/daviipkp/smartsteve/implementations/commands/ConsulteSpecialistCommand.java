package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.smartsteve.Instance.CommandE;
import com.daviipkp.smartsteve.services.CommandRegistry;
import org.springframework.stereotype.Component;

@Component
public class ConsulteSpecialistCommand extends CommandE {
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
        return "Consult a specialized AI for Coding or Math problems. If this command is used, tell the user to wait. Example usage: " +  CommandRegistry.getExampleUsage(getID(), "How do I set up a Java Spring Auth Configuration?");
    }
}
