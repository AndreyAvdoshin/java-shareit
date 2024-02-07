package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemService itemService;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserService userService,
                                  ItemService itemService) {
        this.itemRequestRepository = itemRequestRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {

        User requestor = userService.returnUserIfExists(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(requestor);

        log.info("Создание запроса - {}", itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    public List<ItemRequestDto> getRequests(Long userId) {

        userService.checkUserIfExists(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        List<Item> items = itemService.getAllWithRequests();

        return itemRequests.stream()
                .map(itemRequest -> {
                    List<ItemWithRequestDto> itemWithRequests = items.stream()
                            .filter(item -> item.getRequest().getId().equals(itemRequest.getId()))
                            .map(item -> new ItemWithRequestDto(item, itemRequest.getId()))
                            .collect(Collectors.toList());

                    ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
                    itemRequestDto.setItems(itemWithRequests);
                    return itemRequestDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userService.checkUserIfExists(userId);

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(returnItemRequestIfExists(requestId));
        List<ItemWithRequestDto> itemWithRequestDtos = itemService.getAllByRequestId(requestId);
        itemRequestDto.setItems(itemWithRequestDtos == null ? new ArrayList<>() : itemWithRequestDtos);

        log.info("Получение запроса по id - {} пользователем - {} запрос {}", requestId, userId, itemRequestDto);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(int from, int size, Long userId) {
        userService.checkUserIfExists(userId);

        PageRequest pageRequest = PageRequest.of(from / size, size);

        return itemRequestRepository.findAllByRequestorIdIsNot(userId, pageRequest)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .peek(itemRequestDto -> {
                    List<ItemWithRequestDto> itemWithRequestDtos = itemService.getAllByRequestId(itemRequestDto.getId());
                    itemRequestDto.setItems(itemWithRequestDtos);
                })
                .collect(Collectors.toList());
    }

    private ItemRequest returnItemRequestIfExists(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос по id - " + requestId + " не найден"));
    }
}
