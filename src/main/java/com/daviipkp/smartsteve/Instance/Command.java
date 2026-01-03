package com.daviipkp.smartsteve.Instance;

import com.daviipkp.smartsteve.services.LLMService;
import com.daviipkp.smartsteve.services.VoiceService;
import com.daviipkp.smartsteve.implementations.commands.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Component
public abstract class Command {

    @Getter
    private String[] arguments;

    @Getter
    @Setter
    private boolean autoCallback;

    @Getter
    @Setter
    private boolean ShouldUseSupCallback;

    @Getter
    @Setter
    private Supplier<Void> supCallback;

    private static List<Command> commands;
    private static List<String> commandNames;

    public abstract void execute();

    public abstract void callback();

    public abstract void executeSupCallback();

    public abstract String getID();

    public abstract String getDescription();

    public Command setArguments(String[] arg0) {
        this.arguments = arg0;
        return this;
    }

}