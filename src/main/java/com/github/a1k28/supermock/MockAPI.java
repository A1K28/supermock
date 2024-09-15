package com.github.a1k28.supermock;

import com.github.a1k28.interceptoragent.InterceptorAPI;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;

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
    public static void attachAgentToThisJVM(List<String> whitelistedClasses) {
        if (!agentLoaded) {
            synchronized (MockAPI.class) {
                if (!agentLoaded) {
                    DynamicAgentLoader.loadAgent(whitelistedClasses);

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
            for (int i = 0; i < args.length; i++) {
                if (args[i].getClass() != method.getParameterTypes()[i]) continue outer;
            }
            return method;
        }
        throw new RuntimeException("Declared method: " + methodName + " not found for class: " + clazz);
    }
}
