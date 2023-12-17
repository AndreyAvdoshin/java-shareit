package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getAllByUserId(Long userId);

    Item create(Item item);

    Item getById(Long id);

    Item update(Item item);

    void delete(Long id);

    List<Item> search(String text);
}
