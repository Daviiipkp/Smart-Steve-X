package com.daviipkp.smartsteve;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String extractJsonField(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(\"[^\"]*\"|null|true|false)");
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            String value = matcher.group(1);
            if (value.startsWith("\"") && value.endsWith("\"")) {
                return value.substring(1, value.length() - 1);
            }
            return value;
        }
        return null;
    }

    public static String extractInnerJson(String fullResponse) {
        Pattern pattern = Pattern.compile("\"content\"\\s*:\\s*\"(.*?)(?<!\\\\)\"", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(fullResponse);
        if (matcher.find()) {
            return matcher.group(1).replace("\\n", "\n").replace("\\\"", "\"");
        }
        return fullResponse;
    }

    public static String extractField(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(\"[^\"]*\"|null)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            String val = matcher.group(1);
            if (val.startsWith("\"")) {
                return val.substring(1, val.length() - 1); // Remove aspas
            }
            return val;
        }
        return null;
    }

}
