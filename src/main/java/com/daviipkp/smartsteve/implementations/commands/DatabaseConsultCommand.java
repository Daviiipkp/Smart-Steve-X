package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveJsoning.annotations.CommandDescription;

@CommandDescription(value = "Use to check for user requests regarded to past interactions. IMPORTANT: Check Memory Similarity Consult before asking for this command. ",
        possibleArguments = {"request: <String>", "context: <String>"},
        exampleUsage = "request: Business Idea from User about selling software\ncontext: User asked me to explain the business idea he had about selling software. I consulted it on database. Generate a response based on what was found.")
public class DatabaseConsultCommand extends WebRequestTriggeredCommand {

    @Override
    public boolean checkTrigger() {
        return false;
    }

    @Override
    public void handleError(Exception e) {

    }

    @Override
    public String getDescription() {
        return "";
    }
}
