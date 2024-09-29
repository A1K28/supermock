package com.github.a1k28.supermock;

import com.github.a1k28.interceptoragent.ArgumentType;
import com.github.a1k28.interceptoragent.InterceptorAPI;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;

import static com.github.a1k28.supermock.ClassHelper.getMethods;

@RequiredArgsConstructor
public class MockAPI {
    private static volatile boolean agentLoaded = false;
    private static InterceptorAPI interceptorAPI;

    private final List<Method> methods;
    private final Object[] args;

    public static MockAPI when(Class clazz, String methodName, Object... args) {
        List<Method> methods = getMethods(clazz, methodName, args);
        Object[] mockedArgs = new Object[args.length];
        for (int i = 0; i < mockedArgs.length; i++) {
            if (args[i] != null && args[i].getClass() == MockType.class) mockedArgs[i] = ArgumentType.ANY;
            else mockedArgs[i] = args[i];
        }
        return new MockAPI(methods, mockedArgs);
    }

    public void thenReturn(Object object) {
        for (Method method : methods)
            interceptorAPI.mockMethodReturns(method, object, args);
    }

    public void thenReturnStub(Class clazz) {
        for (Method method : methods)
            interceptorAPI.mockMethodReturnStub(clazz, method, args);
    }

    public void thenReturnVoid() {
        for (Method method : methods)
            interceptorAPI.mockMethodReturns(method, null, args);
    }

    public void thenThrow(Class exceptionType) {
        for (Method method : methods)
            interceptorAPI.mockMethodThrows(method, exceptionType, args);
    }

    public static MockType any() {
        return MockType.ANY;
    }

    public static MockType stub() {
        return MockType.STUB;
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
}
