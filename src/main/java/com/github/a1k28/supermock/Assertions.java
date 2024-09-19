package com.github.a1k28.supermock;

import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ReflectionDiffBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Assertions {
    public static void assertEquals(Object o1, Object o2) {
        if (o1 == o2) return;
        if (o1.getClass() != o2.getClass())
            throw new AssertionError("Objects are not equal");
        DiffResult diffResult = new ReflectionDiffBuilder<>(o1, o2, ToStringStyle.JSON_STYLE).build();
        int diffNum = diffResult.getNumberOfDiffs();
        if (diffNum > 0)
            throw new AssertionError("Objects are not equal. diff num: " + diffNum);
    }
}
