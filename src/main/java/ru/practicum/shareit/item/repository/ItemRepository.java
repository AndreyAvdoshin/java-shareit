package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getAllByUserId(Long userId);

    Item create(Item item);

    Item getById(Long id);

    Item update(Item item);

    List<Item> search(String text);
}
