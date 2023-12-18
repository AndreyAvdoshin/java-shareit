package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


public interface ItemService {

    List<ItemDto> getAllByUserId(Long userId);

    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto getById(Long id, Long userID);

    ItemDto update(ItemDto itemDto, Long userId);

    void delete(Long id);

    List<ItemDto> search(String text);

}
