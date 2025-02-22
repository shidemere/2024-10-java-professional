package ru.otus.processor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotation.After;
import ru.otus.annotation.Before;
import ru.otus.annotation.Test;
import ru.otus.util.TestStatistic;

public class TestProcessor<T> {
    private static final Logger logger = LoggerFactory.getLogger(TestProcessor.class);

    public TestProcessor(Class<T> clazz) {

        TestStatistic statistic = new TestStatistic();
        T t;

        try {
            t = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Method[] declaredMethods = t.getClass().getDeclaredMethods();

        invokeMethodsByAnnotation(t, method -> method.isAnnotationPresent(Before.class), declaredMethods, statistic);
        invokeMethodsByAnnotation(t, method -> method.isAnnotationPresent(Test.class), declaredMethods, statistic);
        invokeMethodsByAnnotation(t, method -> method.isAnnotationPresent(After.class), declaredMethods, statistic);

        if (statistic.getAllTestCounter() == 0) {
            logger.warn("Class is not annotated with annotations for testing.");
        }

        logger.info("Testing is done");
        logger.info("All tests: {}", statistic.getAllTestCounter());
        logger.info("Success tests: {}", statistic.getSuccessCounter());
        logger.info("Failed tests: {}", statistic.getFailCounter());
    }

    private void invokeMethodsByAnnotation(
            Object target, Predicate<Method> predicate, Method[] method, TestStatistic statistic) {
        List<Method> methodList = Arrays.stream(method).filter(predicate).toList();
        methodList.forEach(m -> invokeWithCounter(target, m, statistic));
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
