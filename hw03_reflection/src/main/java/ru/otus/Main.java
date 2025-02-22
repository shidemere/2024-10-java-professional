package ru.otus;

import java.lang.reflect.InvocationTargetException;
import ru.otus.processor.TestProcessor;
import ru.otus.test.CatClinicTestClass;
import ru.otus.test.DogClinicTestClass;

public class Main {
    public static void main(String[] args)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        TestProcessor<CatClinicTestClass> catClinicTestClassTestProcessor =
                new TestProcessor<>(CatClinicTestClass.class);
        TestProcessor<DogClinicTestClass> dogClinicTestClassTestProcessor =
                new TestProcessor<>(DogClinicTestClass.class);
    }
}
