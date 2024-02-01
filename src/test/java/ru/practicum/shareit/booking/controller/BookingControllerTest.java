package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = {"db.name=test"})
@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private UserDto user;
    private ItemDto item;
    private BookingDto bookingDto;
    private BookingOutputDto bookingOutputDto;
    private BookingOutputDto otherBookingOutputDto;

    @BeforeEach
    void startUp() {

        user = UserDto.builder()
                .id(1L)
                .name("User")
                .email("user@user.ru")
                .build();

        item = ItemDto.builder()
                .id(1L)
                .name("Компьютер")
                .description("Хороший компьютер")
                .available(true)
                .build();

        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusHours(6))
                .build();

        bookingOutputDto = BookingOutputDto.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusHours(6))
                .status(Status.APPROVED)
                .build();

        otherBookingOutputDto = BookingOutputDto.builder()
                .id(2L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusHours(6))
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void shouldCreateBooking() throws Exception {
        when(bookingService.create(any(), anyLong())).thenReturn(bookingOutputDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingOutputDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingOutputDto.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutputDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingOutputDto.getItem().getId()), Long.class));

        verify(bookingService, times(1)).create(bookingDto, 1L);
    }

    @Test
    void shouldSetApprovedStatus() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingOutputDto);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutputDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingOutputDto.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutputDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingOutputDto.getItem().getId()), Long.class));

        verify(bookingService, times(1)).approve(1L, 1L, true);
    }

    @Test
    void shouldGetBookingById() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingOutputDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutputDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingOutputDto.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutputDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingOutputDto.getItem().getId()), Long.class));

        verify(bookingService, times(1)).getBooking(1L, 1L);
    }

    @Test
    void shouldGetBookingsByBookerId() throws Exception {
        when(bookingService.getBookingsByBookerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutputDto, otherBookingOutputDto));

        mvc.perform(get("/bookings", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingOutputDto, otherBookingOutputDto))));

        verify(bookingService, times(1)).getBookingsByBookerId(1L, "ALL", 0, 10);
    }

    @Test
    void shouldGetBookingsByUserId() throws Exception {
        when(bookingService.getBookingsByUserId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutputDto, otherBookingOutputDto));

        mvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingOutputDto, otherBookingOutputDto))));

        verify(bookingService, times(1)).getBookingsByUserId(1L, "ALL", 0, 10);
    }

}