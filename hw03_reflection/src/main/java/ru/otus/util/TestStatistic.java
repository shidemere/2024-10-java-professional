package ru.otus.util;

public class TestStatistic {
    private int successCounter = 0;
    private int failCounter = 0;
    private int allTestCounter = 0;

    public void incrementSuccess() {
        ++successCounter;
    }

    public void incrementFail() {
        ++failCounter;
    }

    public void incrementAll() {
        ++allTestCounter;
    }

    public int getSuccessCounter() {
        return successCounter;
    }

    public int getFailCounter() {
        return failCounter;
    }

    public int getAllTestCounter() {
        return allTestCounter;
    }
}
