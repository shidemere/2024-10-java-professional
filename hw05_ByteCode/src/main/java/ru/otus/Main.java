package ru.otus;

import ru.otus.logging.TestLoggingInterface;
import ru.otus.logging.TestLoggingProxy;

public class Main {
    public static void main(String[] args) {
        TestLoggingInterface logging = TestLoggingProxy.createTestLogging();
        logging.calculaction(1); // не логгируется
        logging.calculaction(1, 2); // логгируется
        logging.calculaction(1, 2, 3); // логгируется
    }
}
