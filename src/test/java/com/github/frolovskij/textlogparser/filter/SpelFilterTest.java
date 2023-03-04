package com.github.frolovskij.textlogparser.filter;

import com.github.frolovskij.textlogparser.LogEvent;
import com.github.frolovskij.textlogparser.LogEventAdapter;
import com.github.frolovskij.textlogparser.LogIterator;
import com.github.frolovskij.textlogparser.Schema;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

class SpelFilterTest {

    @Test
    void test() throws IOException {
        Filter filter = new SpelFilter("#f1 == '1' && #f2 == 'A' && #f3 == 'xxx'");

        Schema schema = getSchema();
        LogEvent event = getLogEvent(schema);
        LogEventAdapter adapter = new LogEventAdapter(schema);

        adapter.setLogEvent(event);

        Assertions.assertTrue(filter.filter(adapter));
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
