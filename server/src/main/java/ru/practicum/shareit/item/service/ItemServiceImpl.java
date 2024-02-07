package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserService userService,
                           BookingRepository bookingRepository, CommentRepository commentRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public List<ItemOutputDto> getAllByUserId(Long userId, int from, int size) {

        userService.checkUserIfExists(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<Item> items = itemRepository.findByOwnerIdOrderById(userId, pageRequest);

        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(userId);

        log.info("Получение списка вещей пользователя по id - {} : {}", userId, items);

        return items.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookings.stream()
                            .filter(booking -> booking.getItem().getId().equals(item.getId()))
                            .collect(Collectors.toList());

                    Booking lastBooking = itemBookings.stream()
                            .filter(booking -> booking.getStatus().equals(Status.APPROVED) &&
                                    booking.getStart().isBefore(LocalDateTime.now()))
                            .max(Comparator.comparing(Booking::getStart))
                            .orElse(null);

                    Booking nextBooking = itemBookings.stream()
                            .filter(booking -> booking.getStatus().equals(Status.APPROVED) &&
                                    booking.getStart().isAfter(LocalDateTime.now()))
                            .min(Comparator.comparing(Booking::getStart))
                            .orElse(null);

                    ItemOutputDto itemOutputDto = new ItemOutputDto(item,
                            lastBooking == null ? null : BookingMapper.toBookingShortDto(lastBooking),
                            nextBooking == null ? null : BookingMapper.toBookingShortDto(nextBooking));

                    itemOutputDto.setComments(CommentMapper.toCommentsDto(
                            commentRepository.findCommentsByItemIdWithUserAndItem(item.getId())));

                    return itemOutputDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemWithRequestDto create(ItemWithRequestDto itemWithRequestDto, Long userId) {

        Item item = ItemMapper.toItem(itemWithRequestDto);
        item.setOwner(userService.returnUserIfExists(userId));
        if (itemWithRequestDto.getRequestId() != null) {
            item.setRequest(itemRequestRepository
                    .findById(itemWithRequestDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос по id - " + itemWithRequestDto.getRequestId() +
                            " не найдена")));
        }

        log.info("Создание вещи - {}", item);
        return ItemMapper.toItemWithRequestDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getById(Long itemId, Long userId) {

        userService.checkUserIfExists(userId);
        Item item = returnItemIfExists(itemId);
        ItemOutputDto itemOutputDto;

        if (item.getOwner().getId().equals(userId)) {
            List<Booking> bookings = bookingRepository.findAllByItemOwnerId(userId);

            Booking lastBooking = bookings.stream()
                    .filter(booking -> booking.getStatus().equals(Status.APPROVED) &&
                            booking.getStart().isBefore(LocalDateTime.now()))
                    .max(Comparator.comparing(Booking::getStart))
                    .orElse(null);

            Booking nextBooking = bookings.stream()
                    .filter(booking -> booking.getStatus().equals(Status.APPROVED) &&
                            booking.getStart().isAfter(LocalDateTime.now()))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);

            itemOutputDto = new ItemOutputDto(item,
                    lastBooking == null ? null : BookingMapper.toBookingShortDto(lastBooking),
                    nextBooking == null ? null : BookingMapper.toBookingShortDto(nextBooking));

        } else {
            itemOutputDto = new ItemOutputDto(item, null, null);
        }

        itemOutputDto.setComments(CommentMapper.toCommentsDto(
                commentRepository.findCommentsByItemIdWithUserAndItem(itemId)));

        log.info("Получение вещи пользователя по id - {} : {}", userId, itemOutputDto);
        return itemOutputDto;
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId, Long itemId) {

        userService.checkUserIfExists(userId);
        Item updatedItem = returnItemIfExists(itemId);
        if (!updatedItem.getOwner().getId().equals(userId)) {
            throw new NotOwnerException("Запрещено редактировать не свою вещь");
        }
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }

        updatedItem = itemRepository.save(updatedItem);
        log.info("Обновление вещи по id - {} пользователем - {} : {}", itemId, userId, updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public List<ItemDto> search(String text, int from, int size) {
        if (!text.isEmpty() && !Pattern.matches("^[\\sа-яА-Яa-zA-Z0-9]+$", text)) {
            throw new IncorrectParameterException("text");
        } else if (text.isEmpty()) {
            return new ArrayList<>();
        }

        PageRequest pageRequest = PageRequest.of(from, size);

        log.info("Поиск вещи по строке - {}", text);
        return itemRepository.search(text, pageRequest).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {

        User author = userService.returnUserIfExists(userId);
        Item item = returnItemIfExists(itemId);

        Booking booking = bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndIsBeforeOrderByEndDesc(itemId,
                userId,
                Status.APPROVED,
                LocalDateTime.now());

        if (booking == null) {
            throw new NotAvailableException("Бронирование вещи по id - " + itemId + " пользователем - " +
                    userId + " не найдено");
        }

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);

        log.info("Сооздание комментария - {}", comment);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<Item> getAllWithRequests() {
        return itemRepository.getAllWithRequests();
    }

    @Override
    public List<ItemWithRequestDto> getAllByRequestId(Long requestId) {
        return itemRepository.getAllByRequestId(requestId).stream()
                .map(item -> new ItemWithRequestDto(item, requestId))
                .collect(Collectors.toList());
    }

    @Override
    public Item getFirstByUserId(Long userId) {
        return itemRepository.findFirstByOwnerId(userId);
    }

    @Override
    public Item returnItemIfExists(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь по id - " + itemId + " не найдена"));
    }
}
