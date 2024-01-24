package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */

@Slf4j
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
                                   @RequestBody @Valid BookingDto bookingDto) {
        log.info("Запрос создания бронирования пользователем id - {}", userId);
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto approveByUser(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam("approved") boolean approved) {
        log.info("Запрос подтверждения бронирования по id - {} пользователем id - {} подтверждение - {}",
                bookingId, userId, approved);
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getBookingById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                       @PathVariable Long bookingId) {
        log.info("Запрос бронирования по id - {} пользователем id - {}", bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutputDto> getBookingsByBookerId(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                        @RequestParam(name = "state",
                                                              defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0", required = false) int from,
                                                        @RequestParam(defaultValue = "10", required = false) int size) {
        log.info("Запрос всех бронирований по пользователю id - {} со статусом - {} со страницы - {} количеством - {}",
                userId, state, from, size);
        return bookingService.getBookingsByBookerId(userId, state, from, size);
    }

    @GetMapping
    @RequestMapping("/owner")
    public List<BookingOutputDto> getBookingsByUserId(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                      @RequestParam(name = "state",
                                                                defaultValue = "ALL") String state,
                                                      @RequestParam(defaultValue = "0", required = false) int from,
                                                      @RequestParam(defaultValue = "10", required = false) int size) {
        log.info("Запрос всех бронирований по владельцу id - {} со статусом - {} со страницы - {} количеством - {}",
                userId, state, from, size);
        return bookingService.getBookingsByUserId(userId, state, from, size);
    }
}
