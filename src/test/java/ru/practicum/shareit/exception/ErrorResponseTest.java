package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    void shouldTestExceptionText() {
        String error = "Ошибка";
        String description = "Описчание ошибки";

        ErrorResponse errorResponse = new ErrorResponse(error, description);

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getError()).isEqualTo(error);
        assertThat(errorResponse.getDescription()).isEqualTo(description);
    }

}