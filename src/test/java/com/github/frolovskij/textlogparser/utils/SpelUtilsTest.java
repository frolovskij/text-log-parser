package com.github.frolovskij.textlogparser.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class SpelUtilsTest {

    @Test
    void testGetReferencedVariablesInExpressionWithoutVariables() {
        Expression expression = SpelUtils.compileExpression("1 * 2");
        Set<String> referencedVariables = SpelUtils.getReferencedVariables(expression);
        Assertions.assertTrue(referencedVariables.isEmpty());
    }

    @Test
    void testGetReferencedVariables() {
        Expression expression = SpelUtils.compileExpression("#timestamp + ' ' + #level");
        Set<String> referencedVariables = SpelUtils.getReferencedVariables(expression);
        Assertions.assertEquals(new HashSet<>(Arrays.asList("timestamp", "level")), referencedVariables);
    }

}
