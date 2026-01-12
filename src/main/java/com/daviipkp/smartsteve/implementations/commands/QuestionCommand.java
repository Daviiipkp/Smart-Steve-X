package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.QueuedCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@CommandDescription(value = "",
        exampleUsage = "")
public class QuestionCommand extends QueuedCommand {
    @Override
    public void handleError(Exception e) {

    }
}
