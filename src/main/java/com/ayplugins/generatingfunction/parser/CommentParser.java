package com.ayplugins.generatingfunction.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/****
 @author Ay
 @date  - 12:04
 @version 1.0
 */
public class CommentParser {

    public static class ParseResult {
        public String methodName;
        public String returnType = "void";
        public List<Param> params = new ArrayList<>();
        public List<String> exceptions = new ArrayList<>();
    }

    public static class Param {
        public String type;
        public String name;

        Param(String type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    public static ParseResult parse(String comment) {
        ParseResult result = new ParseResult();
        String[] lines = comment.split("\n");

        for (String line : lines) {
            line = line.trim().replaceAll("^//+", "").replaceAll("^\\*+", "").trim();
            extractDescription(line, result);
            extractParam(line, result);
            extractReturn(line, result);
            extractThrows(line, result);
        }
        return result;
    }

    private static void extractDescription(String line, ParseResult result) {
        Matcher matcher = Pattern.compile("@description:?\\s+([A-Za-z][A-Za-z0-9_]*)").matcher(line);
        if (matcher.find()) result.methodName = matcher.group(1).trim();
    }

    private static void extractParam(String line, ParseResult result) {
        //Matcher matcher = Pattern.compile("@param:?\\s+(\\S+)\\s+(\\S+)").matcher(line);
        Matcher matcher =Pattern.compile("@param:?\\s+([\\w<>]+)\\s+([\\w<>]+)").matcher(line);
        while  (matcher.find()) {
            result.params.add(new Param(matcher.group(1), matcher.group(2)));
        }
    }

    private static void extractReturn(String line, ParseResult result) {
        Matcher matcher = Pattern.compile("@return:?\\s+(\\S+)").matcher(line);
        if (matcher.find()) result.returnType = matcher.group(1).trim();
    }

    private static void extractThrows(String line, ParseResult result) {
        Matcher matcher = Pattern.compile("@throws:?\\s+(\\S+)").matcher(line);
        if (matcher.find()) result.exceptions.add(matcher.group(1).trim());
    }
}
