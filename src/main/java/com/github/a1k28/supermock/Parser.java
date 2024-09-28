package com.github.a1k28.supermock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;

public class Parser {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String serialize(Object object) {
        if (object == null) return null;
        if (object instanceof String) return wrap((String) object, String.class);
        String res = gson.toJson(object);
        return wrap(res, object.getClass());
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

    private static String wrap(String str, Class clazz) {
        if (isPrimitive(clazz))
            return "\"" + str + "\"";
        return "\"\"\"\n" + str + "\"\"\"";
    }

    private static boolean isPrimitive(Class clazz) {
        return clazz == Boolean.class || clazz == boolean.class ||
                clazz == Byte.class || clazz == byte.class ||
                clazz == Short.class || clazz == short.class ||
                clazz == Integer.class || clazz == int.class ||
                clazz == Long.class || clazz == long.class ||
                clazz == Float.class || clazz == float.class ||
                clazz == Double.class || clazz == double.class ||
                clazz == Character.class || clazz == char.class;
    }
}
