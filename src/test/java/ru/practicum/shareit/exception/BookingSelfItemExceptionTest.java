package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BookingSelfItemExceptionTest {

    @Test
    void shouldExceptionThrowing() {
        String expectedMessage = "Текст ошибки";

        BookingSelfItemException exception = assertThrows(BookingSelfItemException.class, () -> {
            throw new BookingSelfItemException(expectedMessage);
        });

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

}