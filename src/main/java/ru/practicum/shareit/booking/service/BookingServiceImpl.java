package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.utils.Validation;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    public BookingServiceImpl(BookingRepository bookingRepository, UserService userService, ItemService itemService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public BookingOutputDto create(BookingDto bookingDto, Long userId) {
        Validation.checkPositiveId(User.class, userId);

        User booker = userService.returnUserIfExists(userId);
        Item item = itemService.returnItemIfExists(bookingDto.getItemId());

        if (!item.isAvailable()) {
            throw new NotAvailableException("Вещь с id - " + item.getId() + " недоступна на данный момент");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Бронирование собственной вещь недопустимо");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new IncorrectParameterException("start");
        }
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new IncorrectParameterException("end");
        }

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);

        log.info("Создание бронирования - {}", booking);
        return BookingMapper.toBookingOutputDto(bookingRepository.save(booking));
    }

    @Override
    public BookingOutputDto approve(Long userId, Long bookingId, boolean approve) {
        Validation.checkPositiveId(User.class, userId);
        Validation.checkPositiveId(Booking.class, bookingId);

        Booking booking = returnBookingIfExists(bookingId);
        userService.checkUserIfExists(userId);

        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new IncorrectParameterException(String.valueOf(approve));
        }

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Бронирование по id - " + bookingId +
                    " не найдено для пользователя по id - " + userId);
        }

        if (approve) {
            booking.setStatus(Status.APPROVED);

        } else {
            booking.setStatus(Status.REJECTED);
        }

        log.info("Подтверждение бронирования - {}", booking);
        return BookingMapper.toBookingOutputDto(bookingRepository.save(booking));
    }

    @Override
    public BookingOutputDto getBooking(Long userId, Long bookingId) {
        Validation.checkPositiveId(User.class, userId);
        Validation.checkPositiveId(Booking.class, bookingId);

        Booking booking = returnBookingIfExists(bookingId);
        userService.checkUserIfExists(userId);

        if (booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            log.info("Получение бронирования по id - {} пользователем по id - {}", bookingId, userId);
            return BookingMapper.toBookingOutputDto(booking);
        } else {
            throw new NotFoundException("Бронирование по id - " + bookingId +
                    " не найдено для пользователя по id - " + userId);
        }
    }

    @Override
    public List<BookingOutputDto> getBookingsByBookerId(Long userId, String state, int from, int size) {
        Validation.checkPositiveId(User.class, userId);
        Validation.checkPositivePagination(from, size);

        PageRequest pageRequest = PageRequest.of(from / size, size);

        userService.checkUserIfExists(userId);

        List<Booking> bookings;
        LocalDateTime currentTime = LocalDateTime.now();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        currentTime, currentTime, pageRequest);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, currentTime,
                        pageRequest);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, currentTime,
                        pageRequest);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING,
                        pageRequest);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED,
                        pageRequest);
                break;
            default:
                throw new UnsupportedStatusException(state);
        }

        log.info("Получение списка бронирований пользователя по id - {} со статусом - {} : {}",
                userId, state, bookings);

        return bookings.stream()
                .map(BookingMapper::toBookingOutputDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingOutputDto> getBookingsByUserId(Long userId, String state, int from, int size) {
        Validation.checkPositiveId(User.class, userId);
        Validation.checkPositivePagination(from, size);
        userService.checkUserIfExists(userId);

        if (itemService.getFirstByUserId(userId) == null) {
            throw new NotAvailableException("У пользователя по id - " + userId + " нет вещей");
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<Booking> bookings;
        LocalDateTime currentTime = LocalDateTime.now();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, pageRequest);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        currentTime, currentTime, pageRequest);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, currentTime,
                        pageRequest);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, currentTime,
                        pageRequest);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING,
                        pageRequest);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED,
                        pageRequest);
                break;
            default:
                throw new UnsupportedStatusException(state);
        }

        log.info("Получение списка бронирований владельца по id - {} со статусом - {} : {}",
                userId, state, bookings);

        return bookings.stream()
                .map(BookingMapper::toBookingOutputDto)
                .collect(Collectors.toList());
    }

    private Booking returnBookingIfExists(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование по id - " + bookingId + " не найдено"));
    }
}
