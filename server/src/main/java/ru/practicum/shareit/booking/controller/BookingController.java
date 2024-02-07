package ru.practicum.shareit.booking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingOutputDto create(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                   @RequestBody BookingDto bookingDto) {
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto approveByUser(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam("approved") boolean approved) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getBookingById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                           @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutputDto> getBookingsByBookerId(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                        @RequestParam(name = "state",
                                                              defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0") Integer from,
                                                        @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.getBookingsByBookerId(userId, state, from, size);
    }

    @GetMapping
    @RequestMapping("/owner")
    public List<BookingOutputDto> getBookingsByUserId(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                      @RequestParam(name = "state",
                                                                defaultValue = "ALL") String state,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.getBookingsByUserId(userId, state, from, size);
    }
}
