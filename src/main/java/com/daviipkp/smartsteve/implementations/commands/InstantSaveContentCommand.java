package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.smartsteve.Constants;
import com.daviipkp.smartsteve.services.SpringContext;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@CommandDescription(value = "Use to save important content on database. Anything saved here might be consulted in the future.",
        possibleArguments = "content: <String>",
        exampleUsage = "content: User mother's birthday date is November 17th\ncontent: Startup Protocol is about doing this or that")
public class InstantSaveContentCommand extends InstantCommand {

    public InstantSaveContentCommand() {
        setCommand(() -> {
            VectorStore vectorStore = SpringContext.getBean(VectorStore.class);

            String content = getArgument("content");

            if (content == null || content.trim().isEmpty()) {
                SteveCommandLib.systemPrint("no content");
                return;
            }

            Map<String, Object> d = Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "tipo", "comando_manual"
            );

            Document doc = new Document(content, d);



            try {
                vectorStore.add(List.of(doc));
                if(Constants.MEMORY_DEBUG) {
                    SteveCommandLib.systemPrint("saved: " + content);
                }
            } catch (Exception e) {
                e.printStackTrace();
                SteveCommandLib.systemPrint("Error: " + e.getMessage());
            }

        });
    }
}
