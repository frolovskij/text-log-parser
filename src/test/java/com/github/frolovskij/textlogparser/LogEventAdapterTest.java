package com.github.frolovskij.textlogparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

class LogEventAdapterTest {

    @Test
    void test() throws IOException {
        Schema schema = getSchema();
        LogEvent event = getLogEvent(schema);
        LogEventAdapter adapter = new LogEventAdapter(schema);

        adapter.setLogEvent(event);

        Assertions.assertEquals("1", adapter.group("f1"));
        Assertions.assertEquals("A", adapter.group("f2"));
        Assertions.assertEquals("xxx", adapter.group("f3"));
    }

    @Test
    void testTypedGetters() throws IOException {
        Schema schema = getSchema();
        LogEvent event = getLogEvent(schema);
        LogEventAdapter adapter = new LogEventAdapter(schema);

        adapter.setLogEvent(event);

        Assertions.assertEquals("1", adapter.group("f1"));
        Assertions.assertEquals("1", adapter.group(1));
        Assertions.assertEquals(1, adapter.asInt("f1"));
        Assertions.assertEquals(1, adapter.asInt(1));
        Assertions.assertEquals(1L, adapter.asLong("f1"));
        Assertions.assertEquals(1L, adapter.asLong(1));

        Assertions.assertEquals("A", adapter.group("f2"));
        Assertions.assertEquals("A", adapter.group(2));
        Assertions.assertEquals("A", adapter.asString("f2"));
        Assertions.assertEquals("A", adapter.asString(2));

        Assertions.assertEquals("xxx", adapter.group("f3"));
        Assertions.assertEquals("xxx", adapter.group(3));
    }

    private Schema getSchema() {
        Schema schema = new Schema();
        schema.setMultiLineSupport(false);
        schema.setSingleLineRegex("^(?<f1>\\d+) (?<f2>\\S+) (?<f3>.+)$");
        return schema;
    }

    private LogEvent getLogEvent(Schema schema) throws IOException {
        LogEvent event;
        try (BufferedReader reader = new BufferedReader(new StringReader("1 A xxx"))) {
            LogIterator iterator = new LogIterator(reader, schema);
            event = iterator.next();
        }
        return event;
    }

}
