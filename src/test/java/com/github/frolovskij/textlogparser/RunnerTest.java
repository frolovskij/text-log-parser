package com.github.frolovskij.textlogparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;

public class RunnerTest {

    public static final String TEST_SCHEMA = "src/test/resources/test-schema.yaml";
    public static final String TEST_LOG = "src/test/resources/test.log";

    @Test
    void testRunWithoutParams() throws Exception {
        String systemOut = tapSystemOut(() ->
                Runner.main(new String[]{}));
        Assertions.assertTrue(systemOut.contains("Missing required options"));
    }

    @Test
    void testRunWithNonExistingSchema() throws Exception {
        String systemOut = tapSystemOut(() ->
                Runner.main(new String[]{
                        "--" + RunnerParams.OPT_SCHEMA.getLongOpt(),
                        "this_schema_file_does_not_exists.yaml",
                        "--" + RunnerParams.OPT_INPUT_FILE.getLongOpt(),
                        TEST_LOG
                }));
        Assertions.assertTrue(systemOut.contains("Schema file not found: this_schema_file_does_not_exists.yaml"));
    }

    @Test
    void testRunWithNonIncorrectSchema() throws Exception {
        String systemOut = tapSystemOut(() ->
                Runner.main(new String[]{
                        "--" + RunnerParams.OPT_SCHEMA.getLongOpt(),
                        TEST_LOG,
                        "--" + RunnerParams.OPT_INPUT_FILE.getLongOpt(),
                        TEST_LOG
                }));
        Assertions.assertTrue(systemOut.contains("Error reading schema"));
    }

    @Test
    void testSimpleEchoToStdout() throws Exception {
        String actual = tapSystemOut(() ->
                Runner.main(new String[]{
                        "--" + RunnerParams.OPT_SCHEMA.getLongOpt(),
                        TEST_SCHEMA,
                        "--" + RunnerParams.OPT_INPUT_FILE.getLongOpt(),
                        TEST_LOG
                }));
        String expected = new String(Files.readAllBytes(Paths.get(TEST_LOG)), StandardCharsets.UTF_8);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testSimpleEchoToFile() throws Exception {
        Path output = Files.createTempFile("log-parser-test-", ".log");
        try {
            Runner.main(new String[]{
                    "--" + RunnerParams.OPT_SCHEMA.getLongOpt(),
                    TEST_SCHEMA,
                    "--" + RunnerParams.OPT_INPUT_FILE.getLongOpt(),
                    TEST_LOG,
                    "--" + RunnerParams.OPT_OUTPUT_FILE.getLongOpt(),
                    output.toAbsolutePath().toString()
            });
            String expected = new String(Files.readAllBytes(Paths.get(TEST_LOG)), StandardCharsets.UTF_8);
            String actual = new String(Files.readAllBytes(output), StandardCharsets.UTF_8);
            Assertions.assertEquals(expected, actual);
        } finally {
            Files.deleteIfExists(output);
        }
    }

    @Test
    void testFilter() throws Exception {
        String actual = tapSystemOut(() ->
                Runner.main(new String[]{
                        "--" + RunnerParams.OPT_SCHEMA.getLongOpt(),
                        TEST_SCHEMA,
                        "--" + RunnerParams.OPT_INPUT_FILE.getLongOpt(),
                        TEST_LOG,
                        "--" + RunnerParams.OPT_INPUT_FILTER.getLongOpt(),
                        "group('logger') == 'com.example.demo.DemoApplication'"
                }));
        String expected = "2023-02-14T22:14:30.296+03:00  INFO 1428 --- [           main] " +
                "com.example.demo.DemoApplication         : " +
                "Starting DemoApplication using Java 17.0.3 with PID 1428" +
                System.lineSeparator() +
                "2023-02-14T22:14:30.298+03:00  INFO 1428 --- [           main] " +
                "com.example.demo.DemoApplication         : " +
                "No active profile set, falling back to 1 default profile: \"default\"" +
                System.lineSeparator() +
                "2023-02-14T22:14:33.440+03:00  INFO 1428 --- [           main] " +
                "com.example.demo.DemoApplication         : " +
                "Started DemoApplication in 3.523 seconds (process running for 4.001)" +
                System.lineSeparator();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testFilterWithFormatter() throws Exception {
        String actual = tapSystemOut(() ->
                Runner.main(new String[]{
                        "--" + RunnerParams.OPT_SCHEMA.getLongOpt(),
                        TEST_SCHEMA,
                        "--" + RunnerParams.OPT_INPUT_FILE.getLongOpt(),
                        TEST_LOG,
                        "--" + RunnerParams.OPT_INPUT_FILTER.getLongOpt(),
                        "group('level') == 'WARN'",
                        "--" + RunnerParams.OPT_OUTPUT_FORMAT.getLongOpt(),
                        "'timestamp = \"' + group('timestamp') + '\"'"
                }));
        String expected = "timestamp = \"2023-02-14T22:14:32.178+03:00\"" +
                System.lineSeparator() +
                "timestamp = \"2023-02-14T22:14:33.016+03:00\"" +
                System.lineSeparator();
        Assertions.assertEquals(expected, actual);
    }

}
