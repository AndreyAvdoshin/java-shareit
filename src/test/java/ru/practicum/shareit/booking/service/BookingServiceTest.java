package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"db.name=test"})
class BookingServiceTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private ItemServiceImpl itemService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private BookingRepository bookingRepository;

    private User user;
    private User owner;
    private Item item;
    private ItemDto itemDto;
    private UserDto userDto;
    private Booking booking;
    private Booking otherBooking;
    private BookingOutputDto bookingOutputDto;
    private BookingDto bookingDto;


    @BeforeEach
    void startUp() {

        user = User.builder()
                .id(1L)
                .name("User")
                .email("user@user.ru")
                .build();

        owner = User.builder()
                .id(2L)
                .name("Owner")
                .email("owner@user.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Компьютер")
                .description("Хороший компьютер")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(owner)
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusHours(6))
                .status(Status.APPROVED)
                .build();

        itemDto = ItemMapper.toItemDto(item);
        userDto = UserMapper.toUserDto(user);

        otherBooking = Booking.builder()
                .id(2L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusHours(6))
                .status(Status.WAITING)
                .build();

        bookingOutputDto = BookingOutputDto.builder()
                .id(1L)
                .item(itemDto)
                .booker(userDto)
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusHours(6))
                .status(Status.APPROVED)
                .build();

        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 7, 5, 0, 0))
                .end(LocalDateTime.of(2023, 10, 12, 0, 0))
                .build();

    }

    @Test
    void shouldCreateBooking() {
        when(itemService.returnItemIfExists(anyLong())).thenReturn(item);
        when(userService.returnUserIfExists(anyLong())).thenReturn(user);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        bookingOutputDto = bookingService.create(bookingDto, user.getId());

        assertEquals(bookingOutputDto, BookingMapper.toBookingOutputDto(booking));

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCreateBookingByOwner() {
        when(itemService.returnItemIfExists(anyLong())).thenReturn(item);
        when(userService.returnUserIfExists(anyLong())).thenReturn(user);

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingDto, owner.getId()));
    }

    @Test
    void shouldThrowNotAvailableExceptionWhenCreateBookingNotAvailable() {
        when(itemService.returnItemIfExists(anyLong())).thenReturn(item);
        when(userService.returnUserIfExists(anyLong())).thenReturn(user);

        item.setAvailable(false);

        assertThrows(NotAvailableException.class, () -> bookingService.create(bookingDto, user.getId()));
    }

    @Test
    void shouldThrowIncorrectParameterExceptionWhenStartIsAfterEnd() {
        when(itemService.returnItemIfExists(anyLong())).thenReturn(item);
        when(userService.returnUserIfExists(anyLong())).thenReturn(user);

        bookingDto.setStart(bookingDto.getEnd().plusHours(1));

        assertThrows(IncorrectParameterException.class, () -> bookingService.create(bookingDto, user.getId()));
    }

    @Test
    void shouldThrowIncorrectParameterExceptionWhenStartEqualsEnd() {
        when(itemService.returnItemIfExists(anyLong())).thenReturn(item);
        when(userService.returnUserIfExists(anyLong())).thenReturn(user);

        bookingDto.setStart(bookingDto.getEnd());

        assertThrows(IncorrectParameterException.class, () -> bookingService.create(bookingDto, user.getId()));
    }

    @Test
    void shouldApproveBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        booking.setStatus(Status.WAITING);

        bookingOutputDto = bookingService.approve(owner.getId(), booking.getId(), true);
        assertEquals(bookingOutputDto.getStatus(), Status.APPROVED);

        booking.setStatus(Status.WAITING);
        bookingOutputDto = bookingService.approve(owner.getId(), booking.getId(), false);
        assertEquals(bookingOutputDto.getStatus(), Status.REJECTED);

        verify(bookingRepository, times(2)).save(any(Booking.class));
    }

    @Test
    void shouldThrowIncorrectParameterExceptionWhenAlreadyApproved() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(IncorrectParameterException.class,
                () -> bookingService.approve(owner.getId(), booking.getId(), true));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenNotOwnerApprove() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        booking.setStatus(Status.WAITING);

        assertThrows(NotFoundException.class,
                () -> bookingService.approve(user.getId(), booking.getId(), true));
    }

    @Test
    void shouldGetBookingByOwnerId() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        bookingOutputDto = bookingService.getBooking(owner.getId(), booking.getId());

        assertEquals(bookingOutputDto, BookingMapper.toBookingOutputDto(booking));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenNotOwnerGetBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(user.getId(), booking.getId()));
    }

    @Test
    void shouldGetAllBookingsByBookerId() {
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingOutputDto> bookingOutputDtos = bookingService.getBookingsByBookerId(owner.getId(), "ALL", 1, 1);
        assertEquals(bookingOutputDtos, List.of(BookingMapper.toBookingOutputDto(booking)));

        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        bookingOutputDtos = bookingService.getBookingsByBookerId(owner.getId(), "CURRENT", 1, 1);
        assertEquals(bookingOutputDtos, List.of(BookingMapper.toBookingOutputDto(booking)));

        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class),
                any(PageRequest.class))).thenReturn(List.of(booking));
        bookingOutputDtos = bookingService.getBookingsByBookerId(owner.getId(), "PAST", 1, 1);
        assertEquals(bookingOutputDtos, List.of(BookingMapper.toBookingOutputDto(booking)));

        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class),
                any(PageRequest.class))).thenReturn(List.of(booking));
        bookingOutputDtos = bookingService.getBookingsByBookerId(owner.getId(), "FUTURE", 1, 1);
        assertEquals(bookingOutputDtos, List.of(BookingMapper.toBookingOutputDto(booking)));

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class),
                any(PageRequest.class))).thenReturn(List.of(otherBooking));
        bookingOutputDtos = bookingService.getBookingsByBookerId(owner.getId(), "WAITING", 1, 1);
        assertEquals(bookingOutputDtos, List.of(BookingMapper.toBookingOutputDto(otherBooking)));

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class),
                any(PageRequest.class))).thenReturn(List.of(otherBooking));
        bookingOutputDtos = bookingService.getBookingsByBookerId(owner.getId(), "REJECTED", 1, 1);
        assertEquals(bookingOutputDtos, List.of(BookingMapper.toBookingOutputDto(otherBooking)));

    }

    @Test
    void shouldGetAllBookingsByUserId() {
        when(itemService.getFirstByUserId(anyLong())).thenReturn(item);

        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking));

        List<BookingOutputDto> bookingOutputDtos = bookingService.getBookingsByUserId(user.getId(), "ALL", 1, 1);
        assertEquals(bookingOutputDtos, List.of(BookingMapper.toBookingOutputDto(booking)));

        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        bookingOutputDtos = bookingService.getBookingsByUserId(user.getId(), "CURRENT", 1, 1);
        assertEquals(bookingOutputDtos, List.of(BookingMapper.toBookingOutputDto(booking)));

        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class),
                any(PageRequest.class))).thenReturn(List.of(booking));
        bookingOutputDtos = bookingService.getBookingsByUserId(owner.getId(), "PAST", 1, 1);
        assertEquals(bookingOutputDtos, List.of(BookingMapper.toBookingOutputDto(booking)));

        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class),
                any(PageRequest.class))).thenReturn(List.of(booking));
        bookingOutputDtos = bookingService.getBookingsByUserId(owner.getId(), "FUTURE", 1, 1);
        assertEquals(bookingOutputDtos, List.of(BookingMapper.toBookingOutputDto(booking)));

        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class),
                any(PageRequest.class))).thenReturn(List.of(otherBooking));
        bookingOutputDtos = bookingService.getBookingsByUserId(owner.getId(), "WAITING", 1, 1);
        assertEquals(bookingOutputDtos, List.of(BookingMapper.toBookingOutputDto(otherBooking)));

        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class),
                any(PageRequest.class))).thenReturn(List.of(otherBooking));
        bookingOutputDtos = bookingService.getBookingsByUserId(owner.getId(), "REJECTED", 1, 1);
        assertEquals(bookingOutputDtos, List.of(BookingMapper.toBookingOutputDto(otherBooking)));

    }

    @Test
    void shouldThrowUnsupportedStatusExceptionWhenStatusUnsupportedByBooker() {
        assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getBookingsByBookerId(user.getId(), "ALLES", 1, 1));
    }

    @Test
    void shouldThrowUnsupportedStatusExceptionWhenStatusUnsupportedByUser() {
        when(itemService.getFirstByUserId(anyLong())).thenReturn(item);

        assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getBookingsByUserId(user.getId(), "ALLES", 1, 1));
    }

    @Test
    void shouldThrowNotAvailableExceptionWhenUserNotHaveItems() {
        assertThrows(NotAvailableException.class,
                () -> bookingService.getBookingsByUserId(user.getId(), "ALL", 1, 1));
    }
}