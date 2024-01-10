package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(final MethodArgumentNotValidException e) {
        return Map.of("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler(UniqueViolatedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> uniqueViolatedResponse(UniqueViolatedException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFoundException(final RuntimeException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(NotOwnerException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> notOwnerException(final RuntimeException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(IncorrectParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIncorrectParameterException(final IncorrectParameterException e) {
        return Map.of("error",
                String.format("Ошибка с полем \"%s\".", e.getParameter())
        );
    }

    @ExceptionHandler(NotAvailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> notAvailableException(final RuntimeException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(UnsupportedStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> unsupportedStatusException(final RuntimeException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(BookingSelfItemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> bookingSelfItemException(final RuntimeException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleServerError(final Throwable e) {
        return Map.of("error", e.getMessage());
    }
}
