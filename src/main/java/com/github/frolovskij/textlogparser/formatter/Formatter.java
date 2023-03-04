package com.github.frolovskij.textlogparser.formatter;

import com.github.frolovskij.textlogparser.LogEventAdapter;

public interface Formatter {

    String format(LogEventAdapter event);

}
