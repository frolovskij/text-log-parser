package com.github.frolovskij.textlogparser.utils;

import com.github.frolovskij.textlogparser.filter.SpelFilter;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.ast.VariableReference;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class SpelUtils {

    private SpelUtils() {
    }

    public static Expression compileExpression(String expression) {
        SpelParserConfiguration config = new SpelParserConfiguration(
                SpelCompilerMode.IMMEDIATE,
                SpelFilter.class.getClassLoader());
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser(config);
        return spelExpressionParser.parseExpression(expression);
    }

    public static Set<String> getReferencedVariables(Expression expr) {
        Set<String> names = new HashSet<>();
        SpelNode ast = ((SpelExpression) expr).getAST();
        walkAst(ast, node -> {
            if (node instanceof VariableReference) {
                String variableName = node.toStringAST().replace("#", "");
                names.add(variableName);
            }
        });
        return names;
    }

    private static void walkAst(SpelNode node, Consumer<SpelNode> consumer) {
        for (int i = 0; i < node.getChildCount(); i++) {
            SpelNode child = node.getChild(i);
            consumer.accept(child);
            walkAst(child, consumer);
        }
    }

}
