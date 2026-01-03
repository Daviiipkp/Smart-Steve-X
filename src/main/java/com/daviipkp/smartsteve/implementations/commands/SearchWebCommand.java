package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.smartsteve.Instance.Command;
import lombok.Getter;

public class SearchWebCommand extends Command {

    @Getter
    private static final SearchWebCommand instance = new SearchWebCommand();


    @Override
    public void execute() {

        callback();
    }

    @Override
    public void callback() {

    }

    @Override
    public void executeSupCallback() {
        //Logic before sup
        this.getSupCallback().get();
    }

    @Override
    public String getID() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getDescription() {
        return "Command to search anything on Web.";
    }

}
