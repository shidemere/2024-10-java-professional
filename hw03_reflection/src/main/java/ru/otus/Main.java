package ru.otus;

import ru.otus.processor.TestProcessor;
import ru.otus.test.CatClinicTestClass;
import ru.otus.test.DogClinicTestClass;

public class Main {
    public static void main(String[] args) {
        TestProcessor<CatClinicTestClass> catClinicTestClassTestProcessor =
                new TestProcessor<>(CatClinicTestClass.class);
        TestProcessor<DogClinicTestClass> dogClinicTestClassTestProcessor =
                new TestProcessor<>(DogClinicTestClass.class);
    }
}
