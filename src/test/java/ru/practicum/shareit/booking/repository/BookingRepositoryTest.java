package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Item item;
    private User user;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("User")
                .email("email@email.ru")
                .build();

        item = Item.builder()
                .name("Вещь")
                .description("Хорошая очень")
                .available(true)
                .owner(user)
                .build();

        user = userRepository.save(user);
        item = itemRepository.save(item);

        booking = Booking.builder()
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
    }

    @AfterEach
    void afterEach() {
        bookingRepository.deleteAll();
    }

    @Test
    void shouldFindAllBookingsByBookerId() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));

        booking = bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(user.getId(), PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void shouldFindAllBookingsByBookerIdAndStartBeforeAndEndAfter() {
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));

        booking = bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                user.getId(), LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void shouldFindAllBookingsByBookerIdAndStartBefore() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));

        booking = bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                user.getId(), LocalDateTime.now(), PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void shouldFindAllBookingsByBookerIdAndStartAfter() {
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(3));

        booking = bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                user.getId(), LocalDateTime.now(), PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void shouldFindAllBookingsByBookerIdAndStatus() {
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(3));

        booking = bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                user.getId(), Status.WAITING, PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void shouldFindAllBookingsByItemOwner() {
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(3));

        booking = bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(
                user.getId(), PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void shouldFindAllBookingsByItemOwnerAndStartBeforeAndEndAfter() {
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));

        booking = bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                user.getId(), LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void shouldFindAllBookingsByItemOwnerAndEndBefore() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));

        booking = bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                user.getId(), LocalDateTime.now(), PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void shouldFindAllBookingsByItemOwnerAndStartAfter() {
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(3));

        booking = bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                user.getId(), LocalDateTime.now(), PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void shouldFindAllBookingsByItemOwnerAndStatus() {
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(3));

        booking = bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                user.getId(), Status.WAITING, PageRequest.of(0, 10));

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    void shouldFindFirstBookingsByItemAndBookerAndStatusAndEndIsBefore() {
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(3));

        booking = bookingRepository.save(booking);
        Booking savedBooking = bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndIsBeforeOrderByEndDesc(
                item.getId(), user.getId(), Status.WAITING, LocalDateTime.now());

        assertEquals(booking, savedBooking);
    }


}