package ru.otus.util;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import ru.otus.annotation.After;
import ru.otus.annotation.Before;
import ru.otus.annotation.Test;

public class MethodContainer {

    private final List<Method> beforeMethods = new LinkedList<>();
    private final List<Method> afterMethods = new LinkedList<>();
    private final List<Method> testMethods = new LinkedList<>();

    public MethodContainer(Method[] methods) {
        for (Method method : methods) {
            if (method.isAnnotationPresent(Before.class)) {
                beforeMethods.add(method);
            }
            if (method.isAnnotationPresent(After.class)) {
                afterMethods.add(method);
            }
            if (method.isAnnotationPresent(Test.class)) {
                testMethods.add(method);
            }
        }
    }

    public List<Method> getBeforeMethods() {
        return beforeMethods;
    }

    public List<Method> getAfterMethods() {
        return afterMethods;
    }

    public List<Method> getTestMethods() {
        return testMethods;
    }
}
