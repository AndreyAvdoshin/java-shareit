package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start,
                                                                           LocalDateTime end, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end,
                                                                PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start,
                                                                 PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime start,
                                                                                LocalDateTime end,
                                                                                PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end,
                                                                   PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start,
                                                                    PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long userId, Status status, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerId(Long ownerId);

    Booking findFirstByItemIdAndBookerIdAndStatusAndEndIsBeforeOrderByEndDesc(Long itemId, Long ownerId, Status status,
                                                                             LocalDateTime date);
}
