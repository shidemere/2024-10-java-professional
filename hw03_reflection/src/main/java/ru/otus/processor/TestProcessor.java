package ru.otus.processor;

import java.lang.reflect.Method;
import ru.otus.annotation.After;
import ru.otus.annotation.Before;
import ru.otus.annotation.Test;

public class TestProcessor<T> {
    private final T t;
    private int successCounter;
    private int failCounter;
    private int allTestCounter;

    public TestProcessor(Class<T> clazz) {

        try {
            t = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.isAnnotationPresent(Before.class)) {
                processBeforeAnnotation(method);
            } else if (method.isAnnotationPresent(Test.class)) {
                processTestAnnotation(method);
            } else if (method.isAnnotationPresent(After.class)) {
                processAfterAnnotation(method);
            }
        }

        if (successCounter == 0 && failCounter == 0) {
            System.out.println("Class is not annotated with annotations for testing.");
        }

        System.out.println("Testing is done");
        System.out.printf("All tests: %s\n", allTestCounter);
        System.out.printf("Success tests: %s\n", successCounter);
        System.out.printf("Failed tests: %s\n", failCounter);
    }

    private void processBeforeAnnotation(Method method) {
        allTestCounter++;
        try {
            method.invoke(t);
            successCounter++;
        } catch (Exception e) {
            failCounter++;
        }
    }

    private void processTestAnnotation(Method method) {
        allTestCounter++;
        try {
            method.invoke(t);
            successCounter++;
        } catch (Exception e) {
            failCounter++;
        }
    }

    private void processAfterAnnotation(Method method) {
        allTestCounter++;
        try {
            method.invoke(t);
            successCounter++;
        } catch (Exception e) {
            failCounter++;
        }
    }
}
