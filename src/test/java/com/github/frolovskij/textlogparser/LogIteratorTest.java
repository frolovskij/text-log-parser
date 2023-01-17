package com.github.frolovskij.textlogparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class LogIteratorTest {

    @Test
    void testSingleLineLog() throws Exception {
        Schema schema = new Schema();
        schema.setMultiLineSupport(false);
        schema.setSingleLineRegex("^(?<f1>\\d+) (?<f2>\\S+) (?<f3>.+)$");

        List<LogEvent> events = new ArrayList<>();
        Path path = Paths.get("src/test/resources/single_line_log.log");
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            LogIterator iterator = new LogIterator(reader, schema);
            while (iterator.hasNext()) {
                LogEvent next = iterator.next();
                events.add(next);
            }
        }

        Assertions.assertEquals(3, events.size());

        LogEventAdapter adapter = new LogEventAdapter(schema);

        adapter.setLogEvent(events.get(0));
        Assertions.assertEquals("1", adapter.group("f1"));
        Assertions.assertEquals("A", adapter.group("f2"));
        Assertions.assertEquals("xxx", adapter.group("f3"));

        adapter.setLogEvent(events.get(1));
        Assertions.assertEquals("2", adapter.group("f1"));
        Assertions.assertEquals("B", adapter.group("f2"));
        Assertions.assertEquals("yyy", adapter.group("f3"));

        adapter.setLogEvent(events.get(2));
        Assertions.assertEquals("3", adapter.group("f1"));
        Assertions.assertEquals("C", adapter.group("f2"));
        Assertions.assertEquals("zzz", adapter.group("f3"));
    }

    @Test
    void testMultiLineLog() throws Exception {
        Schema schema = new Schema();
        schema.setMultiLineSupport(true);
        schema.setSingleLineRegex("^(?<f1>\\d+) (?<f2>\\S+) (?<f3>.+)$");
        schema.setMultiLineRegex("(?s)^(?<f1>\\d+) (?<f2>\\S+) (?<f3>.+)$");

        List<LogEvent> events = new ArrayList<>();
        Path path = Paths.get("src/test/resources/multi_line_log.log");
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            LogIterator iterator = new LogIterator(reader, schema);
            while (iterator.hasNext()) {
                LogEvent next = iterator.next();
                events.add(next);
            }
        }

        LogEventAdapter adapter = new LogEventAdapter(schema);

        Assertions.assertEquals(3, events.size());

        adapter.setLogEvent(events.get(0));
        Assertions.assertEquals("1", adapter.group("f1"));
        Assertions.assertEquals("A", adapter.group("f2"));
        Assertions.assertEquals("xxx1", adapter.group("f3"));

        adapter.setLogEvent(events.get(1));
        Assertions.assertEquals("2", adapter.group("f1"));
        Assertions.assertEquals("B", adapter.group("f2"));
        Assertions.assertEquals(String.format("yyy1%nyyy2%nyyy3"), adapter.group("f3"));

        adapter.setLogEvent(events.get(2));
        Assertions.assertEquals("3", adapter.group("f1"));
        Assertions.assertEquals("C", adapter.group("f2"));
        Assertions.assertEquals(String.format("zzz1%nzzz2"), adapter.group("f3"));
    }

}
