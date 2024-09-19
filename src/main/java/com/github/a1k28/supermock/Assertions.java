package com.github.a1k28.supermock;

import com.google.gson.Gson;

public class Assertions {
    public static void assertEquals(Object o1, Object o2) {
        if (o1 == o2) return;
        String errorMessage = "Objects are not equal.\nExpected: " + o1 + "\n" + "Got: " + o2;
        if (o1.getClass() != o2.getClass())
            throw new AssertionError(errorMessage);
        if (!Parser.serialize(o1).equals(Parser.serialize(o2)))
            throw new AssertionError(errorMessage);
    }
}
