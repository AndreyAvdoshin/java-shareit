package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;

import java.util.List;

public interface BookingService {

    BookingOutputDto create(BookingDto bookingDto, Long userId);

    BookingOutputDto approve(Long userId, Long bookingId, boolean approve);

    BookingOutputDto getBooking(Long userId, Long bookingId);

    List<BookingOutputDto> getBookingsByBookerId(Long userId, String state, int from, int size);

    List<BookingOutputDto> getBookingsByUserId(Long userId, String state, int from, int size);
}
