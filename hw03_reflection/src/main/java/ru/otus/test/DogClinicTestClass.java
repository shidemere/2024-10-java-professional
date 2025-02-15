package ru.otus.test;

import ru.otus.annotation.After;
import ru.otus.annotation.Before;
import ru.otus.annotation.Test;

public class DogClinicTestClass {

    @Before
    public void prepareForWalking() {
        System.out.println("Prepare for dog walking");
    }

    @Test
    public void walk() {
        System.out.println("Dog walking");
    }

    @After
    public void finishWalk() {
        System.out.println("Walking with dog finished");
        // для проверки корректности выводимой статистики
        throw new RuntimeException();
    }
}
