package ru.practicum.shareit.exceptions;

import lombok.Getter;

@Getter
public class ErrorResponse {
    // название ошибки
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }
}
