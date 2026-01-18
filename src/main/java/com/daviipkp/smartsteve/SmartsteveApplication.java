package com.daviipkp.smartsteve;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.Command;
import com.daviipkp.smartsteve.prompt.Prompt;
import com.daviipkp.smartsteve.services.LLMService;
import com.daviipkp.smartsteve.services.SpringContext;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class SmartsteveApplication {

    @Getter
    private static List<Class<? extends Command>> commandList;

    public static void main(String[] args) {
        new SpringApplicationBuilder(SmartsteveApplication.class)
                .headless(false)
                .run(args);
        java.security.Security.setProperty("networkaddress.cache.ttl", "-1");

        if(Configuration.DO_WARM_UP) {
            LLMService.warmUp();
        }

        if(Configuration.CLEAR_MEMO_ON_STARTUP) {
            try {
                JdbcTemplate jdbcTemplate = SpringContext.getBean(JdbcTemplate.class);
                jdbcTemplate.execute("TRUNCATE TABLE vector_store");
                jdbcTemplate.execute("TRUNCATE TABLE chat_message");
                SteveCommandLib.systemPrint("Memory cleared! Change CLEAR_MEMO_ON_STARTUP to false if you don't want this to happen.");
            } catch (Exception e) {
                SteveCommandLib.systemPrint("error clearing memory: " + e.getMessage());
            }
        }

        commandList = Utils.getRegisteredCommands("com.daviipkp.smartsteve.implementations.commands", Configuration.USER_COMMAND_PACKAGE);


        for (String arg : args) {
            if (arg.equals("--FirstBoot")) {
                SpringContext.getBean(LLMService.class).finalCallModel(Prompt.getStartupPrompt());
            }
        }

    }
}
