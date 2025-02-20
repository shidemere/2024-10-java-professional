package ru.otus.processor;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotation.After;
import ru.otus.annotation.Before;
import ru.otus.annotation.Test;

public class TestProcessor<T> {
    private static final Logger logger = LoggerFactory.getLogger(TestProcessor.class);

    public TestProcessor(Class<T> clazz) {

        /*
           Из за запрета на состояние пришлось использовать массив.
           Так как обертки, оказывается, неизменяемы.
           И если я передаю внутрь другого метода обертку - внутри метода создаются новые.
        */
        int[] successCounter = new int[1];
        int[] failCounter = new int[1];
        int[] allTestCounter = new int[1];
        T t;

        try {
            t = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.isAnnotationPresent(Before.class)) {
                invokeWithCounter(t, method, allTestCounter, successCounter, failCounter);
            } else if (method.isAnnotationPresent(Test.class)) {
                invokeWithCounter(t, method, allTestCounter, successCounter, failCounter);
            } else if (method.isAnnotationPresent(After.class)) {
                invokeWithCounter(t, method, allTestCounter, successCounter, failCounter);
            }
        }

        if (successCounter[0] == 0 && failCounter[0] == 0) {
            logger.warn("Class is not annotated with annotations for testing.");
        }

        logger.info("Testing is done");
        logger.info("All tests: {}", allTestCounter[0]);
        logger.info("Success tests: {}", successCounter[0]);
        logger.info("Failed tests: {}", failCounter[0]);
    }

    private void invokeWithCounter(
            Object target, Method method, int[] allTestCounter, int[] successCounter, int[] failCounter) {
        allTestCounter[0]++;
        try {
            method.invoke(target);
            successCounter[0]++;
        } catch (Exception e) {
            failCounter[0]++;
        }
    }
}
