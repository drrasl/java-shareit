package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ExceptionControllerTest {

    @InjectMocks
    private ExceptionController exceptionController;

    @Test
    void handlerDataNotFoundException_shouldReturnErrorResponse() {
        String errorMessage = "Объект не найден";
        DataNotFoundException exception = new DataNotFoundException(errorMessage);

        ErrorResponse response = exceptionController.handlerDataNotFoundException(exception);

        assertThat(response)
                .isNotNull()
                .extracting(ErrorResponse::getError)
                .isEqualTo(errorMessage);
    }

    @Test
    void handlerDuplicateEmailException_shouldReturnErrorResponse() {
        String errorMessage = "Email уже существует";
        DuplicateEmailException exception = new DuplicateEmailException(errorMessage);

        ErrorResponse response = exceptionController.handlerDuplicateEmailException(exception);

        assertThat(response)
                .isNotNull()
                .extracting(ErrorResponse::getError)
                .isEqualTo(errorMessage);
    }

    @Test
    void handlerMissedSmthException_shouldReturnErrorResponse() {
        String errorMessage = "Не указаны обязательные параметры";
        MissedSmthException exception = new MissedSmthException(errorMessage);

        ErrorResponse response = exceptionController.handlerMissedSmthException(exception);

        assertThat(response)
                .isNotNull()
                .extracting(ErrorResponse::getError)
                .isEqualTo(errorMessage);
    }

    @Test
    void handlerWrongDateValidationException_shouldReturnErrorResponse() {
        String errorMessage = "Некорректные даты бронирования";
        WrongDateValidationException exception = new WrongDateValidationException(errorMessage);

        ErrorResponse response = exceptionController.handlerWrongDateValidationException(exception);

        assertThat(response)
                .isNotNull()
                .extracting(ErrorResponse::getError)
                .isEqualTo(errorMessage);
    }

    @Test
    void handlerAccessNotAllowedException_shouldReturnErrorResponse() {
        String errorMessage = "Доступ запрещен";
        AccessNotAllowedException exception = new AccessNotAllowedException(errorMessage);

        ErrorResponse response = exceptionController.handlerAccessNotAllowedException(exception);

        assertThat(response)
                .isNotNull()
                .extracting(ErrorResponse::getError)
                .isEqualTo(errorMessage);
    }
}