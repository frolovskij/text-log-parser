package com.github.frolovskij.textlogparser.filter;

import com.github.frolovskij.textlogparser.LogEventAdapter;
import com.github.frolovskij.textlogparser.utils.SpelUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Set;

public class SpelFilter implements Filter {

    private final Expression expression;
    private final Set<String> referencedVariables;
    private final EvaluationContext context;

    public SpelFilter(String expression) {
        this.expression = SpelUtils.compileExpression(expression);
        this.referencedVariables = SpelUtils.getReferencedVariables(this.expression);
        this.context = new StandardEvaluationContext();
    }

    @Override
    public boolean filter(LogEventAdapter event) {
        updateContext(event);
        return Boolean.TRUE.equals(expression.getValue(context, event, Boolean.class));
    }

    private void updateContext(LogEventAdapter event) {
        for (String variableName : referencedVariables) {
            context.setVariable(variableName, event.group(variableName));
        }
    }

}
