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

    public static MockAPI when(Method method, Object... args) {
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
}
