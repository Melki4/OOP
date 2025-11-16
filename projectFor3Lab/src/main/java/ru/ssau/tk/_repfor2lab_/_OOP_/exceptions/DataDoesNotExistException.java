package ru.ssau.tk._repfor2lab_._OOP_.exceptions;

public class DataDoesNotExistException extends RuntimeException {
    public DataDoesNotExistException(String message) {
        super(message);
    }

    public DataDoesNotExistException() {
        super();
    }
}
