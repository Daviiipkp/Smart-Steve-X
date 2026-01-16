package com.daviipkp.smartsteve.Instance;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.Command;
import com.daviipkp.smartsteve.Utils;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Protocol {

    private String name;
    private Map<String, Map<String, String>> commands = new HashMap<>();

    public Protocol(String name) {
        this.name = name;
    }

    public void addCommand(Class<? extends Command> cmd, Map<String, String> args) {
        commands.put(cmd.getSimpleName(), args);
    }

    public void execute() {
        for(String cmd : commands.keySet()) {
            try {
                Command command = Utils.getCommandByName(cmd);
                for(String argName : commands.get(cmd).keySet()) {
                    try {
                        Field field = command.getClass().getDeclaredField(argName);
                        field.setAccessible(true);

                        field.set(command, commands.get(cmd).get(argName));
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }
                SteveCommandLib.addCommand(command);
            } catch (Exception e) {
                System.out.println("Could find command or it needs a constructor parameter.");
            }
        }
    }

}
