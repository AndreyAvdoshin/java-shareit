package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemWithRequestDtoTest {

    @Autowired
    private JacksonTester<ItemWithRequestDto> json;

    @Test
    void shouldGetSerializeItemWithRequest() throws IOException {
        Item item = Item.builder()
                .id(1L)
                .name("Вещь")
                .description("Отличная вещь")
                .available(true)
                .build();

        User user = User.builder()
                .id(1L)
                .name("User")
                .email("user@email.com")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Очень нужна эта вещь")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        ItemWithRequestDto itemWithRequestDto = new ItemWithRequestDto(item, itemRequest.getId());

        JsonContent<ItemWithRequestDto> bookingDto = json.write(itemWithRequestDto);

        assertThat(bookingDto).hasJsonPath("$.id");
        assertThat(bookingDto).hasJsonPath("$.name");
        assertThat(bookingDto).hasJsonPath("$.description");
        assertThat(bookingDto).hasJsonPath("$.available");
        assertThat(bookingDto).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemRequest.getId().intValue());
    }

}