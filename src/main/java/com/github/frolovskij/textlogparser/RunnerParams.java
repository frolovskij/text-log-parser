package com.github.frolovskij.textlogparser;

import com.github.frolovskij.textlogparser.filter.Filter;
import com.github.frolovskij.textlogparser.filter.SpelFilter;
import com.github.frolovskij.textlogparser.formatter.Formatter;
import com.github.frolovskij.textlogparser.formatter.SpelFormatter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.scanner.ScannerException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RunnerParams {

    static final Option OPT_SCHEMA = Option.builder()
            .longOpt("schema")
            .hasArg()
            .argName("path")
            .desc("Schema file (*.yaml)")
            .required()
            .build();

    static final Option OPT_INPUT_FILE = Option.builder()
            .longOpt("input-file")
            .hasArg()
            .argName("path")
            .desc("Input file")
            .required()
            .build();

    static final Option OPT_INPUT_FILE_ENCODING = Option.builder()
            .longOpt("input-encoding")
            .hasArg()
            .argName("enc")
            .desc("Input encoding")
            .build();

    static final Option OPT_INPUT_FILTER = Option.builder()
            .longOpt("input-filter")
            .hasArg()
            .argName("expr")
            .desc("Filter expression (SpEL)")
            .build();

    static final Option OPT_OUTPUT_FILE = Option.builder()
            .longOpt("output-file")
            .hasArg()
            .argName("path")
            .desc("Output file")
            .build();

    static final Option OPT_OUTPUT_FILE_ENCODING = Option.builder()
            .longOpt("output-encoding")
            .hasArg()
            .argName("enc")
            .desc("Output encoding")
            .build();

    static final Option OPT_OUTPUT_FORMAT = Option.builder()
            .longOpt("output-format")
            .hasArg()
            .argName("expr")
            .desc("Output format expression (SpEL)")
            .build();

    static final Options OPTIONS = new Options()
            .addOption(OPT_SCHEMA)
            .addOption(OPT_INPUT_FILE)
            .addOption(OPT_INPUT_FILE_ENCODING)
            .addOption(OPT_INPUT_FILTER)
            .addOption(OPT_OUTPUT_FILE)
            .addOption(OPT_OUTPUT_FILE_ENCODING)
            .addOption(OPT_OUTPUT_FORMAT);

    private final Schema schema;
    private final Path inputFile;
    private final Charset inputEncoding;
    private final Filter inputFilterExpression;
    private final Path outputFile;
    private final Charset outputEncoding;
    private final Formatter outputFormatExpression;

    public static RunnerParams initFromArgs(String[] args) throws ParseException {
        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine commandLine = commandLineParser.parse(OPTIONS, args);
        return new RunnerParams(commandLine);
    }

    private RunnerParams(CommandLine cmd) {
        this.schema = getSchema(cmd.getOptionValue(OPT_SCHEMA));

        this.inputFile = Paths.get(cmd.getOptionValue(OPT_INPUT_FILE));

        if (cmd.getOptionValue(OPT_INPUT_FILE_ENCODING) != null) {
            this.inputEncoding = Charset.forName(cmd.getOptionValue(OPT_INPUT_FILE_ENCODING));
        } else {
            this.inputEncoding = StandardCharsets.UTF_8;
        }

        if (cmd.getOptionValue(OPT_INPUT_FILTER) != null) {
            this.inputFilterExpression = new SpelFilter(cmd.getOptionValue(OPT_INPUT_FILTER));
        } else {
            this.inputFilterExpression = null;
        }

        if (cmd.getOptionValue(OPT_OUTPUT_FILE) != null) {
            this.outputFile = Paths.get(cmd.getOptionValue(OPT_OUTPUT_FILE));
        } else {
            this.outputFile = null;
        }

        if (cmd.getOptionValue(OPT_OUTPUT_FILE_ENCODING) != null) {
            this.outputEncoding = Charset.forName(cmd.getOptionValue(OPT_OUTPUT_FILE_ENCODING));
        } else {
            this.outputEncoding = StandardCharsets.UTF_8;
        }

        if (cmd.getOptionValue(OPT_OUTPUT_FORMAT) != null) {
            this.outputFormatExpression = new SpelFormatter(cmd.getOptionValue(OPT_OUTPUT_FORMAT));
        } else {
            this.outputFormatExpression = null;
        }
    }

    public Schema getSchema() {
        return schema;
    }

    public Path getInputFile() {
        return inputFile;
    }

    public Charset getInputEncoding() {
        return inputEncoding;
    }

    public Filter getInputFilterExpression() {
        return inputFilterExpression;
    }

    public Path getOutputFile() {
        return outputFile;
    }

    public Charset getOutputEncoding() {
        return outputEncoding;
    }

    public Formatter getOutputFormatExpression() {
        return outputFormatExpression;
    }

    private Schema getSchema(String schemaOptionValue) {
        try {
            PropertyUtils propertyUtils = new PropertyUtils();
            propertyUtils.setSkipMissingProperties(true);
            Constructor constructor = new Constructor(Schema.class);
            constructor.setPropertyUtils(propertyUtils);
            Yaml yaml = new Yaml(constructor);

            Path path = Paths.get(schemaOptionValue);
            if (!Files.exists(path)) {
                throw new RuntimeException("Schema file not found: " + schemaOptionValue);
            }
            return yaml.load(Files.newBufferedReader(path));
        } catch (ScannerException | IOException ex) {
            throw new RuntimeException("Error reading schema", ex);
        }
    }

    public static void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        formatter.setSyntaxPrefix("Usage: ");
        formatter.printHelp("java -jar text-log-parser.jar [OPTIONS]", OPTIONS);
    }

}
