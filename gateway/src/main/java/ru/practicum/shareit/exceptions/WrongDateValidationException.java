package ru.practicum.shareit.exceptions;

public class WrongDateValidationException extends RuntimeException {
    public WrongDateValidationException(String message) {
        super(message);
    }
}
