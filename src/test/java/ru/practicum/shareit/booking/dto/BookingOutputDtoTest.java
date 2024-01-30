package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingOutputDtoTest {

    @Autowired
    private JacksonTester<BookingOutputDto> json;

    @Test
    void shouldGetSerializeBookingOutputDto() throws IOException {
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

        BookingOutputDto bookingOutputDto = BookingOutputDto.builder()
                .id(1L)
                .booker(UserMapper.toUserDto(user))
                .item(ItemMapper.toItemDto(item))
                .status(Status.APPROVED)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .build();

        JsonContent<BookingOutputDto> bookingDto = json.write(bookingOutputDto);

        assertThat(bookingDto).hasJsonPath("$.start");
        assertThat(bookingDto).hasJsonPath("$.end");
        assertThat(bookingDto).hasJsonPath("$.booker");
        assertThat(bookingDto).hasJsonPath("$.item");
        assertThat(bookingDto).hasJsonPathValue("$.start");
        assertThat(bookingDto).hasJsonPathValue("$.end");
        assertThat(bookingDto).extractingJsonPathNumberValue("$.booker.id").isEqualTo(user.getId().intValue());
        assertThat(bookingDto).extractingJsonPathNumberValue("$.item.id").isEqualTo(item.getId().intValue());
    }

}