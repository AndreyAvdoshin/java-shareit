package ru.practicum.shareit.exception;

public class UniqueViolatedException extends RuntimeException{
    public UniqueViolatedException(String message) {
        super(message);
    }
}
