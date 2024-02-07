package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IncorrectParameterExceptionTest {

    @Test
    void shouldCreateIncorrectParameterException() {
        String expectedParameter = "Параемтр с ошибкой";

        IncorrectParameterException exception = new IncorrectParameterException(expectedParameter);

        assertThat(exception).isNotNull();
        assertThat(exception.getParameter()).isEqualTo(expectedParameter);
    }

}