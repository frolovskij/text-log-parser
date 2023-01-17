package com.github.frolovskij.textlogparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEventAdapter {

    private static final Pattern NAMED_GROUP_PATTERN =
            Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>");

    private final Map<String, Integer> indexByName;

    private LogEvent logEvent;

    public LogEventAdapter(Schema schema) {
        List<String> fields = getNamedGroupNames(schema);
        this.indexByName = buildIndexByNameMap(fields);
    }

    public void setLogEvent(LogEvent logEvent) {
        this.logEvent = logEvent;
    }

    public String group(String name) {
        return logEvent.group(indexByName.get(name));
    }

    public String group(int index) {
        return logEvent.group(index);
    }

    public String asString(String name) {
        return group(name);
    }

    public String asString(int index) {
        return group(index);
    }

    public int asInt(String name) {
        return Integer.parseInt(group(name));
    }

    public int asInt(int index) {
        return Integer.parseInt(group(index));
    }

    public long asLong(String name) {
        return Long.parseLong(group(name));
    }

    public long asLong(int index) {
        return Long.parseLong(group(index));
    }

    public String format(String pattern, Object... args) {
        return String.format(pattern, args);
    }

    private Map<String, Integer> buildIndexByNameMap(List<String> fields) {
        Map<String, Integer> indexByName = new HashMap<>();
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            indexByName.put(field, i + 1);
        }
        return indexByName;
    }

    private static List<String> getNamedGroupNames(Schema schema) {
        String regex = schema.isMultiLine() ?
                schema.getMultiLineRegex() :
                schema.getSingleLineRegex();
        List<String> namedGroups = new ArrayList<>();
        Matcher matcher = NAMED_GROUP_PATTERN.matcher(regex);
        while (matcher.find()) {
            namedGroups.add(matcher.group(1));
        }
        return namedGroups;
    }

}
