package ru.otus.logging;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotation.Log;

public class TestLoggingProxy {

    private TestLoggingProxy() {}

    private static final Logger logger = LoggerFactory.getLogger(TestLoggingProxy.class);

    public static TestLoggingInterface createTestLogging() {
        InvocationHandler handler = new LoggingHandler(new TestLoggingInterfaceImpl());
        return (TestLoggingInterface) Proxy.newProxyInstance(
                TestLoggingInterface.class.getClassLoader(), new Class<?>[] {TestLoggingInterface.class}, handler);
    }

    static class LoggingHandler implements InvocationHandler {
        private final TestLoggingInterface testLoggingInterface;
        private final Set<String> methodLoggingSet = new HashSet<>();

        public LoggingHandler(TestLoggingInterface testLoggingInterface) {
            this.testLoggingInterface = testLoggingInterface;

            for (Method method : testLoggingInterface.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Log.class)) {
                    String methodSignature = getMethodSignature(method);
                    methodLoggingSet.add(methodSignature);
                }
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodSignature = getMethodSignature(method);

            if (methodLoggingSet.contains(methodSignature)) {
                logger.info("Log annotation is present on method {}", method.getName());
                StringJoiner sj = new StringJoiner(", ", "invoked method name: " + method.getName() + ", params: ", "");
                for (Object arg : args) {
                    sj.add("param: " + arg);
                }
                logger.info(sj.toString());
            }

            return method.invoke(testLoggingInterface, args);
        }
        private String getMethodSignature(Method method) {
            StringBuilder sb = new StringBuilder();
            sb.append(method.getName());
            for (Class<?> paramType : method.getParameterTypes()) {
                sb.append(":").append(paramType.getName());
            }
            return sb.toString();
        }
    }
}
