package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.UnsupportedStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));
		log.info("Запрос всех бронирований по пользователю id - {} со статусом - {} со страницы - {} количеством - {}",
				userId, state, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
										 @RequestBody @Valid BookingDto requestDto) {
		log.info("Запрос создания бронирования пользователем id - {}", userId);
		return bookingClient.create(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveByUser(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
												@PathVariable @Positive Long bookingId,
												@RequestParam("approved") @NotNull Boolean approved) {
		log.info("Запрос подтверждения бронирования по id - {} пользователем id - {} подтверждение - {}",
				bookingId, userId, approved);
		return bookingClient.approve(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingById(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId,
												 @PathVariable @Positive Long bookingId) {
		log.info("Запрос бронирования по id - {} пользователем id - {}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	@RequestMapping("/owner")
	public ResponseEntity<Object> getBookingsByUserId(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId,
													  @RequestParam(name = "state",
															  defaultValue = "ALL") String stateParam,
													  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
													  @RequestParam(defaultValue = "10") @Positive Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));

		log.info("Запрос всех бронирований по владельцу id - {} со статусом - {} со страницы - {} количеством - {}",
				userId, state, from, size);
		return bookingClient.getBookingsByUserId(userId, state, from, size);
	}

}
