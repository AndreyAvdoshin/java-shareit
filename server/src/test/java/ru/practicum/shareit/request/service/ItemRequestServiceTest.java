package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"db.name=test"})
class ItemRequestServiceTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ItemServiceImpl itemService;

    private User user;
    private User owner;
    private Item item;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemWithRequestDto itemWithRequestDto;

    @BeforeEach
    void setUp() {

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
                .request(itemRequest)
                .build();

        itemWithRequestDto = new ItemWithRequestDto(item, itemRequest.getId());
        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Test
    void shouldCreateItemRequest() {
        when(userService.returnUserIfExists(anyLong())).thenReturn(user);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto outputItemRequest = itemRequestService.create(user.getId(), itemRequestDto);

        assertEquals(outputItemRequest, itemRequestDto);

        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void shouldGetAllRequestsByRequestor() {
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));
        when(itemService.getAllWithRequests()).thenReturn(List.of(item));

        List<ItemRequestDto> outputItemRequests = itemRequestService.getRequests(user.getId());
        itemRequestDto.setItems(List.of(itemWithRequestDto));

        assertEquals(outputItemRequests, List.of(itemRequestDto));

        verify(itemRequestRepository, times(1)).findAllByRequestorIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void shouldGetRequestById() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        itemRequestDto.setItems(new ArrayList<>());
        ItemRequestDto outputItemRequest = itemRequestService.getRequestById(user.getId(), itemRequestDto.getId());

        assertEquals(outputItemRequest, itemRequestDto);

        verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetAllRequests() {
        when(itemRequestRepository.findAllByRequestorIdIsNot(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(itemRequest));

        itemRequestDto.setItems(new ArrayList<>());
        List<ItemRequestDto> items = itemRequestService.getAllRequests(0, 10, owner.getId());

        assertEquals(items, List.of(itemRequestDto));

        verify(itemRequestRepository, times(1))
                .findAllByRequestorIdIsNot(anyLong(), any(PageRequest.class));
    }

}