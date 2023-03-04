package com.github.frolovskij.textlogparser.formatter;

import com.github.frolovskij.textlogparser.LogEventAdapter;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Set;

import static com.github.frolovskij.textlogparser.utils.SpelUtils.compileExpression;
import static com.github.frolovskij.textlogparser.utils.SpelUtils.getReferencedVariables;

public class SpelFormatter implements Formatter {

    private final Expression expression;
    private final Set<String> referencedVariables;
    private final EvaluationContext context;

    public SpelFormatter(String expression) {
        this.expression = compileExpression(expression);
        this.referencedVariables = getReferencedVariables(this.expression);
        this.context = new StandardEvaluationContext();
    }

    @Override
    public String format(LogEventAdapter event) {
        updateContext(event);
        return expression.getValue(context, event, String.class);
    }

    private void updateContext(LogEventAdapter event) {
        for (String variableName : referencedVariables) {
            context.setVariable(variableName, event.group(variableName));
        }
    }

}
