package com.daviipkp.smartsteve.implementations.future;

import com.daviipkp.SteveCommandLib.instance.QueuedCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;

@CommandDescription(value = "",
        exampleUsage = "")
public class QuestionCommand extends QueuedCommand {
    @Override
    public void handleError(Exception e) {

    }
}
