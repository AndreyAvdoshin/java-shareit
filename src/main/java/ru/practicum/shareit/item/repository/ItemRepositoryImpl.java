package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private Long countId = 0L;

    @Override
    public List<Item> getAllByUserId(Long userId) {
        return items.values().stream()
                .filter(i -> i.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item create(Item item) {
        item.setId(++countId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getById(Long id) {
        // здесь тоже должна быть проверка на наличие по id и проверка на id пользователя лучше сделать в сервисе
        return items.get(id);
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(i -> (i.getName().toLowerCase().contains(text) ||
                        i.getDescription().toLowerCase().contains(text)) && i.isAvailable())
                .collect(Collectors.toList());
    }
}
