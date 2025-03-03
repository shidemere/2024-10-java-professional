package ru.otus.jdbc.exception;

public class AnnotationNotPresentException extends RuntimeException {
    public AnnotationNotPresentException(String message) {
        super(message);
    }
}
