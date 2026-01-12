package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;
import com.daviipkp.smartsteve.Constants;
import com.daviipkp.smartsteve.services.SpringContext;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@CommandDescription(value = "Use to save important content on database. Anything saved here might be consulted in the future.",
        exampleUsage = "content: User mother's birthday date is November 17th")
public class SaveContentCommand extends InstantCommand {

    @Describe
    private String content;

    public SaveContentCommand() {
        setCommand(() -> {
            VectorStore vectorStore = SpringContext.getBean(VectorStore.class);

            if (content == null || content.trim().isEmpty()) {
                SteveCommandLib.systemPrint("no content");
                return;
            }

            Map<String, Object> d = Map.of(
                    "timestamp", LocalDateTime.now().toString(),
                    "type", "manual_save"
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
