package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private MockMvc mvc;

    private BookingDto bookingDto;

    @BeforeEach
    public void setUp() {
        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusHours(6))
                .build();
    }

    @Test
    public void shouldGet201WhenCreateBooking() throws Exception {
        String booking = mapper.writeValueAsString(bookingDto);

        when(bookingClient.create(anyLong(), any(BookingDto.class)))
                .thenReturn(new ResponseEntity<>(booking, HttpStatus.CREATED));

        String content = mvc.perform(post("/bookings")
                        .content(booking)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(content, booking);
    }

    @Test
    void shouldGet200WhenApproved() throws Exception {
        mvc.perform(patch("/bookings/{bookingId}?approved={approved}", 1, true)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(bookingClient).approve(1, 1, true);
    }

    @Test
    void shouldGet200WhenGetBookingById() throws Exception {
        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(bookingClient).getBooking(1, 1);
    }

    @Test
    void shouldGet200WhenGetAllBookings() throws Exception {
        mvc.perform(get("/bookings/?state={state}&from={from}&size={size}", BookingState.ALL, 1, 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(bookingClient).getBookings(1, BookingState.ALL, 1, 1);
    }

    @Test
    void getAllByOwner_shouldReturnStatusOk() throws Exception {
        mvc.perform(get("/bookings/owner?state={state}&from={from}&size={size}", BookingState.ALL, 1, 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(bookingClient).getBookingsByUserId(1, BookingState.ALL, 1, 1);
    }

}