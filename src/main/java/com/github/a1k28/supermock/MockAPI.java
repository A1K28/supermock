package com.github.a1k28.supermock;

import com.github.a1k28.interceptoragent.InterceptorAPI;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

@RequiredArgsConstructor
public class MockAPI {
    private static volatile boolean agentLoaded = false;
    private static InterceptorAPI interceptorAPI;

    private final Method method;
    private final Object[] args;

    public static MockAPI when(Class clazz, String methodName, Object... args) {
        Method method = getMethod(clazz, methodName, args);
        return new MockAPI(method, args);
    }

    public void thenReturn(Object object) {
        interceptorAPI.mockMethodReturns(method, object, args);
    }

    public void thenThrow(Class exceptionType) {
        interceptorAPI.mockMethodThrows(method, exceptionType, args);
    }

    public static void resetMockState() {
        interceptorAPI.resetMockState();
    }

    // assumes the VM flag: -Djdk.attach.allowAttachSelf=true
    public static void attachAgentToThisJVM() {
        if (!agentLoaded) {
            synchronized (MockAPI.class) {
                if (!agentLoaded) {
                    DynamicAgentLoader.loadAgent();

                    try {
                        Class<?> reloaderClass = Class.forName("com.github.a1k28.interceptoragent.DynamicInterceptorAgent");
                        Method getInstanceMethod = reloaderClass.getMethod("getInstance");
                        interceptorAPI = (InterceptorAPI) getInstanceMethod.invoke(null);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    agentLoaded = true;
                }
            }
        }
    }

    private static Method getMethod(Class clazz, String methodName, Object[] args) {
        outer: for (Method method : clazz.getDeclaredMethods()) {
            if (!method.getName().equals(methodName)) continue;
            if ((args == null || args.length == 0) ^ method.getParameterCount() == 0) continue;
            if (args == null) return method;
            if (args.length != method.getParameterCount()) continue;
            for (int i = 0; i < args.length; i++) {
                if (!equalsClasses(method.getParameterTypes()[i], args[i].getClass())) continue outer;
            }
            return method;
        }
        throw new RuntimeException("Declared method: " + methodName + " not found for class: " + clazz);
    }

    private static boolean equalsClasses(Class<?> c1, Class<?> c2) {
        if (c1.equals(c2)) return true;
        if (c1.isAssignableFrom(c2)) return true;

        if (c1 == byte.class && c2 == Byte.class) return true;
        if (c2 == byte.class && c1 == Byte.class) return true;

        if (c1 == short.class && c2 == Short.class) return true;
        if (c2 == short.class && c1 == Short.class) return true;

        if (c1 == int.class && c2 == Integer.class) return true;
        if (c2 == int.class && c1 == Integer.class) return true;

        if (c1 == long.class && c2 == Long.class) return true;
        if (c2 == long.class && c1 == Long.class) return true;

        if (c1 == char.class && c2 == Character.class) return true;
        if (c2 == char.class && c1 == Character.class) return true;

        if (c1 == boolean.class && c2 == Boolean.class) return true;
        if (c2 == boolean.class && c1 == Boolean.class) return true;

        if (c1 == float.class && c2 == Float.class) return true;
        if (c2 == float.class && c1 == Float.class) return true;

        if (c1 == double.class && c2 == Double.class) return true;
        if (c2 == double.class && c1 == Double.class) return true;

        return false;
    }
}
