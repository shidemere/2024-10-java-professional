package ru.otus.test;

import ru.otus.annotation.After;
import ru.otus.annotation.Before;
import ru.otus.annotation.Test;

public class CatClinicTestClass {

    @Before
    public void prepareForStroking() {
        System.out.println("Prepare for cat stroking");
    }

    @Test
    public void stroking() {
        System.out.println("Stroking cat");
    }

    @After
    public void finishStroking() {
        System.out.println("Cat is successfully stroked");
    }
}
