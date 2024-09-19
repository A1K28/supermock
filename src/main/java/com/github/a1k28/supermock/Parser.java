package com.github.a1k28.supermock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;

public class Parser {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String serialize(Object object) {
        if (object == null) return null;
        if (object instanceof String) return wrap((String) object);
        if (isPrimitive(object.getClass())) return serializePrimitive(object);
        String res = gson.toJson(object);
        return wrap(res);
    }

    public static Object deserialize(String object, Class<?> clazz) {
        if (object == null) return null;
        if (clazz == String.class) return object;
        Object res = deserializePrimitive(object, clazz);
        if (res != null) return res;
        try {
            return gson.fromJson(object, clazz);
        } catch (Exception e) {
            return Arrays.asList(gson.fromJson(object, clazz.arrayType()));
        }
    }

    private static String wrap(String str) {
        return "\"\"\"\n" + str + "\"\"\"";
    }

    private static String serializePrimitive(Object object) {
        return String.valueOf(object);
    }

    private static boolean isPrimitive(Class<?> c) {
        if (c == byte.class || c == Byte.class) return true;
        if (c == short.class || c == Short.class) return true;
        if (c == int.class || c == Integer.class) return true;
        if (c == long.class || c == Long.class) return true;
        if (c == char.class || c == Character.class) return true;
        if (c == boolean.class || c == Boolean.class) return true;
        if (c == float.class || c == Float.class) return true;
        if (c == double.class || c == Double.class) return true;
        return false;
    }

    private static Object deserializePrimitive(String object, Class c) {
        if (c == byte.class || c == Byte.class) return Byte.valueOf(object);
        if (c == short.class || c == Short.class) return Short.valueOf(object);
        if (c == int.class || c == Integer.class) return Integer.valueOf(object);
        if (c == long.class || c == Long.class) return Long.valueOf(object);
        if (c == char.class || c == Character.class) return object.charAt(0);
        if (c == boolean.class || c == Boolean.class) return Boolean.parseBoolean(object);
        if (c == float.class || c == Float.class) return Float.valueOf(object);
        if (c == double.class || c == Double.class) return Double.valueOf(object);
        return null;
    }
}
