package com.daviipkp.smartsteve.Instance;

import com.daviipkp.smartsteve.services.LLMService;
import com.daviipkp.smartsteve.services.VoiceService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

@Component
public abstract class CommandE {

    @Lazy
    @Autowired
    protected LLMService llmService;

    @Getter
    private String[] arguments = new String[0];

    @Getter
    @Setter
    private boolean autoCallback;

    @Getter
    @Setter
    private boolean ShouldUseSupCallback;

    @Getter
    @Setter
    private Supplier<Void> supCallback;

    @Getter
    private String context;

    private static List<CommandE> commands;
    private static List<String> commandNames;

    public abstract void execute();

    public abstract void callback();

    public abstract void executeSupCallback();

    public void handleError(Exception e) {
        VoiceService.speak(llmService.callDefInstructedModel("", "User asked for command " + this.getID() + " but it failed. Explain it to him.", false).getSteveResponse(), () -> {});
    }

    public abstract String getID();

    public abstract String getDescription();

    public CommandE setArguments(String[] arg0) {
        this.arguments = arg0;
        return this;
    }

    public CommandE setArguments(List<String> arg0) {
        this.arguments = arg0.toArray(new String[arg0.size()]);
        return this;
    }

    public CommandE setContext(String arg0) {
        this.context = arg0;
        return this;
    }

}