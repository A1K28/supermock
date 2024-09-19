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
        if (clazz == String.class)
            return (T) object;
        try {
            try {
                return gson.fromJson(object, clazz);
            } catch (Throwable e) {
                e.printStackTrace();
                return (T) Arrays.asList(gson.fromJson(object, clazz.arrayType()));
            }
        } catch (Throwable e) {
            System.out.println("Could not deserialize: " + object);
            throw e;
        }
    }

    private static String wrap(String str) {
        return "\"\"\"\n" + str + "\"\"\"";
    }
}
