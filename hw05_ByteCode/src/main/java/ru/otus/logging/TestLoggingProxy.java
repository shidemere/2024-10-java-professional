package ru.otus.logging;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotation.Log;

public class TestLoggingProxy {

    private TestLoggingProxy() {}

    private static final Logger logger = LoggerFactory.getLogger(TestLoggingProxy.class);

    public static TestLoggingInterface createTestLogging() {
        InvocationHandler handler = new LoggingHadler(new TestLoggingInterfaceImpl());
        return (TestLoggingInterface) Proxy.newProxyInstance(
                TestLoggingInterface.class.getClassLoader(), new Class<?>[] {TestLoggingInterface.class}, handler);
    }

    static class LoggingHadler implements InvocationHandler {
        private final TestLoggingInterface testLoggingInterface;

        public LoggingHadler(TestLoggingInterface testLoggingInterface) {
            this.testLoggingInterface = testLoggingInterface;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method implMethod = testLoggingInterface.getClass().getMethod(method.getName(), method.getParameterTypes());

            if (implMethod.isAnnotationPresent(Log.class)) {
                logger.info("Log annotation is present on method {}", method.getName());
                StringJoiner sj = new StringJoiner(", ", "invoked method name: " + implMethod.getName() + ", params: ", "");
                for (Object arg : args) {
                    sj.add("param: " + arg);
                }
                logger.info(sj.toString());
            }

            return method.invoke(testLoggingInterface, args);
        }
    }
}
