package com.github.frolovskij.textlogparser;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;

public class LogEvent {

    private final MatchResult matchResult;

    public static LogEvent fromMatcher(Matcher matcher) {
        return new LogEvent(matcher.toMatchResult());
    }

    private LogEvent(MatchResult matchResult) {
        this.matchResult = matchResult;
    }

    public String group(int group) {
        return matchResult.group(group);
    }

}
