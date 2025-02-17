package ru.otus.logging;

import ru.otus.annotation.Log;

public class TestLoggingInterfaceImpl implements TestLoggingInterface {
    @Override
    public void calculaction(int a) {
        System.out.printf("calculaction: %d\n", a);
    }

    @Log
    @Override
    public void calculaction(int a, int b) {
        System.out.printf("calculaction: %d + %d = %d\n", a, b, a + b);
    }

    @Log
    @Override
    public void calculaction(int a, int b, int c) {
        System.out.printf("calculaction: %d + %d + %d = %d\n", a, b, c, a + b + c);
    }
}
