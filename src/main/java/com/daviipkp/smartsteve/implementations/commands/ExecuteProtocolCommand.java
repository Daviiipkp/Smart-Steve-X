package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;
import com.daviipkp.smartsteve.Instance.Protocol;
import com.daviipkp.smartsteve.services.LLMService;
import com.daviipkp.smartsteve.services.SpringContext;

import java.util.Map;

@CommandDescription(value = "Execute a protocol.")
public class ExecuteProtocolCommand extends InstantCommand {

    @Describe
    private String name;

    public ExecuteProtocolCommand() {
        setCommand(new Runnable() {
            @Override
            public void run() {
                LLMService llmS = SpringContext.getBean(LLMService.class);
                Map<Protocol, String> protocols = llmS.getProtocols(name, 1);
                if(protocols.isEmpty()) {
                    throw new RuntimeException("No protocols found with name " + name);
                }
                protocols.keySet().forEach(protocol -> {protocol.execute();});

            }
        });
    }

    @Override
    public void handleError(Exception e) {
        super.handleError(e);
        try {
            CallbackCommand.asError(this.getClass(), e);
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}
