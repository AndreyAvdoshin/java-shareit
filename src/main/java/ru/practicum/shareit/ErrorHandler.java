package ru.practicum.shareit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UniqueViolatedException;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(UniqueViolatedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> uniqueViolatedResponse(UniqueViolatedException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFoundException(final RuntimeException e) {
        return Map.of("error", e.getMessage());
    }
}
