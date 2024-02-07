package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemService {

    List<ItemOutputDto> getAllByUserId(Long userId, int from, int size);

    Item getFirstByUserId(Long userId);

    ItemWithRequestDto create(ItemWithRequestDto itemWithRequestDto, Long userId);

    ItemDto getById(Long id, Long userID);

    ItemDto update(ItemDto itemDto, Long userId, Long itemId);

    List<ItemDto> search(String text, int from, int size);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);

    List<Item> getAllWithRequests();

    List<ItemWithRequestDto> getAllByRequestId(Long requestId);

    Item returnItemIfExists(Long itemId);

}
