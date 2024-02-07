package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"db.name=test"})
class ItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    private User user;
    private User owner;
    private Item item;
    private Item otherItem;
    private Booking booking;
    private Booking otherBooking;
    private Comment comment;
    private CommentDto commentDto;
    private ItemOutputDto itemOutputDto;
    private ItemRequest itemRequest;
    private ItemWithRequestDto itemWithRequestDto;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .email("user@email.ru")
                .name("User")
                .build();

        owner = User.builder()
                .id(2L)
                .email("owne@email.ru")
                .name("Owner")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Очень нужна эта вещь")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1L)
                .name("Вещь")
                .description("Очень классная вещь")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();

        otherItem = Item.builder()
                .id(1L)
                .name("Вещь")
                .description("Очень красивая и классная вещь")
                .available(true)
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .status(Status.APPROVED)
                .build();

        otherBooking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.APPROVED)
                .build();

        comment = Comment.builder()
                .id(1L)
                .author(user)
                .created(LocalDateTime.now())
                .text("Отличная вещь!")
                .build();

        commentDto = CommentMapper.toCommentDto(comment);
        itemOutputDto = new ItemOutputDto(item, BookingMapper.toBookingShortDto(booking),
                BookingMapper.toBookingShortDto(otherBooking));

        itemOutputDto.setComments(CommentMapper.toCommentsDto(List.of(comment)));
        itemWithRequestDto = new ItemWithRequestDto(item, itemRequest.getId());
    }

    @Test
    void shouldGetAllItemByUserId() {
        when(bookingRepository.findAllByItemOwnerId(anyLong())).thenReturn(List.of(booking, otherBooking));
        when(itemRepository.findByOwnerIdOrderById(anyLong(), any(PageRequest.class))).thenReturn(List.of(item));
        when(commentRepository.findCommentsByItemIdWithUserAndItem(anyLong())).thenReturn(List.of(comment));

        List<ItemOutputDto> items = itemService.getAllByUserId(owner.getId(), 0, 10);

        assertEquals(items, List.of(itemOutputDto));

        verify(itemRepository, times(1))
                .findByOwnerIdOrderById(anyLong(), any(PageRequest.class));
    }

    @Test
    void shouldCreateItem() {
        when(userService.returnUserIfExists(anyLong())).thenReturn(user);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemWithRequestDto itemWithRequest = itemService.create(itemWithRequestDto, user.getId());

        assertEquals(itemWithRequest, itemWithRequestDto);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldGetItemById() {
        when(bookingRepository.findAllByItemOwnerId(anyLong())).thenReturn(List.of(booking, otherBooking));
        when(commentRepository.findCommentsByItemIdWithUserAndItem(anyLong())).thenReturn(List.of(comment));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemOutputDto itemDto = (ItemOutputDto) itemService.getById(item.getId(), owner.getId());

        assertEquals(itemDto, itemOutputDto);

        verify(itemRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void shouldUpdateItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(otherItem);

        ItemDto itemDto = itemService.update(ItemMapper.toItemDto(otherItem), owner.getId(), item.getId());

        assertEquals(itemDto, ItemMapper.toItemDto(otherItem));
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldThrowNotOwnerExceptionWhenUpdateTotOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(NotOwnerException.class,
                () -> itemService.update(ItemMapper.toItemDto(otherItem), user.getId(), item.getId()));
    }

    @Test
    void shouldFindItemByText() {
        when(itemRepository.search(anyString(), any(PageRequest.class))).thenReturn(List.of(item));

        List<ItemDto> items = itemService.search("вещь", 0, 10);
        assertEquals(items, List.of(ItemMapper.toItemDto(item)));

        verify(itemRepository, times(1)).search(anyString(), any(PageRequest.class));
    }

    @Test
    void shouldThrowIncorrectParameterExceptionWhenTextIsIncorrect() {
        assertThrows(IncorrectParameterException.class,
                () -> itemService.search("*&^%#$", 0, 10));
    }

    @Test
    void shouldCreateComment() {
        when(userService.returnUserIfExists(anyLong())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndIsBeforeOrderByEndDesc(anyLong(),
                anyLong(), any(Status.class), any(LocalDateTime.class))).thenReturn(booking);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto commentOut = itemService.createComment(user.getId(), item.getId(),
                commentDto);

        assertEquals(commentOut, CommentMapper.toCommentDto(comment));
    }

    @Test
    void shouldGetAllItemsWithRequests() {
        when(itemRepository.getAllWithRequests()).thenReturn(List.of(item));

        List<Item> items = itemService.getAllWithRequests();

        assertEquals(items, List.of(item));
        assertEquals(items.get(0).getRequest(), itemRequest);

        verify(itemRepository, times(1)).getAllWithRequests();
    }

    @Test
    void shouldGelAllItemsByRequestId() {
        when(itemRepository.getAllByRequestId(anyLong())).thenReturn(List.of(item));

        List<ItemWithRequestDto> items = itemService.getAllByRequestId(itemRequest.getId());

        assertEquals(items, List.of(itemWithRequestDto));
        verify(itemRepository, times(1)).getAllByRequestId(anyLong());
    }

    @Test
    void shouldGetFirstItemByOwnerId() {
        when(itemRepository.findFirstByOwnerId(anyLong())).thenReturn(item);

        Item itemOut = itemService.getFirstByUserId(owner.getId());

        verify(itemRepository, times(1)).findFirstByOwnerId(anyLong());
    }

}