package com.github.frolovskij.textlogparser.filter;

import com.github.frolovskij.textlogparser.LogEventAdapter;

public interface Filter {

    boolean filter(LogEventAdapter event);

}
