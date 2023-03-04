package com.github.frolovskij.textlogparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Schema {

    private static final Pattern NAMED_GROUP_PATTERN =
            Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z\\d]*)>");

    private boolean multiLineSupport;
    private String singleLineRegex;
    private String multiLineRegex;

    public boolean isMultiLine() {
        return multiLineSupport;
    }

    public void setMultiLineSupport(boolean multiLineSupport) {
        this.multiLineSupport = multiLineSupport;
    }

    public String getSingleLineRegex() {
        return singleLineRegex;
    }

    public void setSingleLineRegex(String singleLineRegex) {
        this.singleLineRegex = singleLineRegex;
    }

    public String getMultiLineRegex() {
        return multiLineRegex;
    }

    public void setMultiLineRegex(String multiLineRegex) {
        this.multiLineRegex = multiLineRegex;
    }

    public List<String> getNamedGroupNames() {
        String regex = multiLineSupport ? multiLineRegex : singleLineRegex;
        List<String> namedGroups = new ArrayList<>();
        Matcher matcher = NAMED_GROUP_PATTERN.matcher(regex);
        while (matcher.find()) {
            namedGroups.add(matcher.group(1));
        }
        return namedGroups;
    }

}
