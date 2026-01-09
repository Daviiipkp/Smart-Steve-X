package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;

import java.awt.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@CommandDescription(value = "Use to play a specific video or song on YouTube.",
        possibleArguments = "query: <String>",
        exampleUsage = "query: Stressed Out - Twenty One Pilots")
public class InstantPlayOnYoutubeCommand extends InstantCommand {

    public InstantPlayOnYoutubeCommand() {
        setCommand(() -> {
            String songName = getArgument("query");

            if (songName == null || songName.trim().isEmpty()) {
                return;
            }

            try {
                String encodedQuery = URLEncoder.encode(songName + " youtube", StandardCharsets.UTF_8);
                String youtubeUrl = "https://www.youtube.com/results?search_query=" + encodedQuery + "&autoplay=1";
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI(youtubeUrl));
                } else {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + youtubeUrl);
                }

            } catch (Exception e) {
                SteveCommandLib.systemPrint("err: " + e.getMessage());
            }
        });
    }

}
