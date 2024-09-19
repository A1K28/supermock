package com.github.a1k28.supermock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;

public class Parser {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String serialize(Object object) {
        if (object == null) return null;
        if (object instanceof String) return wrap((String) object);
        String res = gson.toJson(object);
        return wrap(res);
    }

    public static <T> T deserialize(String object, Class<T> clazz) {
        if (object == null) return null;
        try {
            return gson.fromJson(object, clazz);
        } catch (Exception e) {
            return (T) Arrays.asList(gson.fromJson((String) object, clazz.arrayType()));
        }
    }

    private static String wrap(String str) {
        return "\"\"\"\n" + str + "\"\"\"";
    }
}
