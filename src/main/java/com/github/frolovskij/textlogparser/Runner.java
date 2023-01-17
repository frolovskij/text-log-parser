package com.github.frolovskij.textlogparser;

import org.apache.commons.cli.ParseException;
import org.springframework.expression.Expression;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;

public class Runner {

    public static void main(String[] args) {
        try {
            RunnerParams params = RunnerParams.initFromArgs(args);
            run(params);
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
            RunnerParams.printUsage();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void run(RunnerParams params) {
        final LogEventAdapter adapter = new LogEventAdapter(params.getSchema());
        final Expression filter = params.getInputFilterExpression();
        final Expression formatter = params.getOutputFormatExpression();

        try (BufferedReader reader = reader(params);
             BufferedWriter writer = writer(params)) {
            LogIterator iterator = new LogIterator(reader, params.getSchema());
            while (iterator.hasNext()) {
                LogEvent event = iterator.next();
                adapter.setLogEvent(event);
                handleEvent(adapter, filter, formatter, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("IO error", e);
        }
    }

    private static BufferedReader reader(RunnerParams params) throws IOException {
        return Files.newBufferedReader(params.getInputFile(), params.getInputEncoding());
    }

    private static BufferedWriter writer(RunnerParams params) throws IOException {
        if (params.getOutputFile() == null) {
            return new BufferedWriter(new OutputStreamWriter(System.out));
        } else {
            return Files.newBufferedWriter(params.getOutputFile(), params.getOutputEncoding());
        }
    }

    private static void handleEvent(LogEventAdapter eventAdapter, Expression filter,
                                    Expression formatter, BufferedWriter bw) throws IOException {
        boolean pass = filter == null || Boolean.TRUE.equals(filter.getValue(eventAdapter, Boolean.class));
        if (!pass) {
            return;
        }
        if (formatter == null) {
            bw.write(eventAdapter.group(0));
            bw.newLine();
        } else {
            String value = formatter.getValue(eventAdapter, String.class);
            if (value != null) {
                bw.write(value);
                bw.newLine();
            }
        }
    }

}
