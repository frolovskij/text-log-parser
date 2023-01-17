# Simple Text Log Parser

## Features

* Single and multi line log events
* Encodings
* Filtering events with Spring Expression Language (SpEL)
* Output reformatting using SpEL

## Usage

```
Usage: java -jar log-parser.jar [OPTIONS]
    --schema <path>           Schema file (*.json)
    --input-file <path>       Input file
    --input-encoding <enc>    Input encoding
    --input-filter <expr>     Filter expression (SpEL)
    --output-file <path>      Output file
    --output-encoding <enc>   Output encoding
    --output-format <expr>    Output format expression (SpEL)
```
## Example

### input.log

```
2023-02-14T22:14:32.178+03:00  WARN 1428 --- [           main] org.hibernate.orm.deprecation            : HHH90000021: Encountered deprecated setting [javax.persistence.sharedCache.mode], use [jakarta.persistence.sharedCache.mode] instead
2023-02-14T22:14:32.341+03:00  INFO 1428 --- [           main] SQL dialect                              : HHH000400: Using dialect: org.hibernate.dialect.H2Dialect
2023-02-14T22:14:32.970+03:00 DEBUG 1428 --- [           main] org.hibernate.SQL                        : drop table if exists foo cascade
Hibernate: drop table if exists foo cascade
```

### schema.yaml

```
multiLineSupport: true
singleLineRegex: ^(?<timestamp>\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d{3}\+\d{2}:\d{2})\s+(?<level>\w+)\s+(?<pid>\d+)\s+---\s+\[\s*(?<thread>[^]]+)]\s+(?<logger>.+?)\s+:\s+(?<message>.*)$
multiLineRegex: (?s)^(?<timestamp>\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d{3}\+\d{2}:\d{2})\s+(?<level>\w+)\s+(?<pid>\d+)\s+---\s+\[\s*(?<thread>[^]]+)]\s+(?<logger>.+?)\s+:\s+(?<message>.*)$
```

### Basic usage

```
java -jar text-log-parser.jar \
    --schema schema.yaml \
    --input-file input.log \
    --input-filter "group('level') == 'WARN'" \
    --output-format "format('%s %s', asString('timestamp'), asString('logger'))"
```

#### Output
```
2023-02-14T22:14:32.178+03:00 org.hibernate.orm.deprecation
```
