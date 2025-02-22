package ru.otus.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.util.MethodCache;
import ru.otus.util.TestStatistic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestProcessor<T> {
    private static final Logger logger = LoggerFactory.getLogger(TestProcessor.class);

    public TestProcessor(Class<T> clazz)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        TestStatistic statistic = new TestStatistic();
        MethodCache cache = new MethodCache(clazz.getDeclaredMethods());

        for (Method testMethod : cache.getTestMethods()) {
            T t = clazz.getDeclaredConstructor().newInstance();
            cache.getBeforeMethods().forEach(method -> invokeWithCounter(t, method, statistic));
            invokeWithCounter(t, testMethod, statistic);
            cache.getAfterMethods().forEach(method -> invokeWithCounter(t, method, statistic));
        }

        // Тогда и логгирование надо сделать иначе. Или нет?
        if (statistic.getAllTestCounter() == 0) {
            logger.warn("Class is not annotated with annotations for testing.");
        }

        logger.info("Testing is done");
        logger.info("All tests: {}", statistic.getAllTestCounter());
        logger.info("Success tests: {}", statistic.getSuccessCounter());
        logger.info("Failed tests: {}", statistic.getFailCounter());
    }

    private void invokeWithCounter(Object target, Method method, TestStatistic statistic) {
        statistic.incrementAll();
        try {
            method.invoke(target);
            statistic.incrementSuccess();
        } catch (Exception e) {
            statistic.incrementFail();
        }
    }
}
