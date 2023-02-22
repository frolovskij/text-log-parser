package com.github.frolovskij.textlogparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogIterator implements Iterator<LogEvent> {

    private final BufferedReader bufferedReader;
    private final Schema schema;

    private final Pattern singleLinePattern;
    private final Pattern multiLinePattern;

    private LogEvent next;

    private boolean needAdvancement;

    private final StringBuilder multiLineEvent = new StringBuilder();

    public LogIterator(BufferedReader bufferedReader, Schema schema) {
        this.bufferedReader = bufferedReader;

        this.schema = Objects.requireNonNull(schema);

        String regex = Objects.requireNonNull(schema.getSingleLineRegex());
        this.singleLinePattern = Pattern.compile(regex);

        if (schema.isMultiLine()) {
            String multiLineRegex = Objects.requireNonNull(schema.getMultiLineRegex());
            this.multiLinePattern = Pattern.compile(multiLineRegex);
        } else {
            this.multiLinePattern = null;
        }

        this.needAdvancement = true;
    }

    @Override
    public boolean hasNext() {
        if (needAdvancement) {
            if (schema.isMultiLine()) {
                readNextFromMultipleLines();
            } else {
                readNextFromSingleLine();
            }
            needAdvancement = false;
        }
        return next != null;
    }

    @Override
    public LogEvent next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        needAdvancement = true;
        return next;
    }

    private void readNextFromSingleLine() {
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Matcher singleLineMatcher = singleLinePattern.matcher(line);
                if (singleLineMatcher.matches()) {
                    next = LogEvent.fromMatcher(singleLineMatcher);
                    break;
                }
            }
            if (line == null) {
                next = null;
            }
        } catch (IOException e) {
            throw new RuntimeException("Unhandled exception", e);
        }
    }

    private void readNextFromMultipleLines() {
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Matcher singleLineMatcher = singleLinePattern.matcher(line);
                if (singleLineMatcher.matches()) {
                    if (multiLineEvent.length() > 0) {
                        Matcher multiMatcher = multiLinePattern.matcher(multiLineEvent.toString());
                        if (multiMatcher.matches()) {
                            next = LogEvent.fromMatcher(multiMatcher);
                            multiLineEvent.setLength(0);
                            multiLineEvent.append(line);
                            break;
                        }
                    } else {
                        multiLineEvent.append(line);
                    }
                } else {
                    if (multiLineEvent.length() == 0) {
                        continue;
                    }
                    multiLineEvent.append(System.lineSeparator());
                    multiLineEvent.append(line);
                }
            }

            if (line == null) {
                if (multiLineEvent.length() > 0) {
                    Matcher multiMatcher = multiLinePattern.matcher(multiLineEvent.toString());
                    if (multiMatcher.matches()) {
                        next = LogEvent.fromMatcher(multiMatcher);
                        multiLineEvent.setLength(0);
                    }
                } else {
                    next = null;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unhandled exception", e);
        }
    }

}
