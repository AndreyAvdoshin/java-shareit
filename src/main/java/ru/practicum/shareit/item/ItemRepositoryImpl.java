package ru.practicum.shareit.item;

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
        // Должна быть проверка на id пользователя будет в сервисе
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
        Item updatedItem = items.get(item.getId());
        if (updatedItem == null) {
            // написать проверку на наличие вещи по id
            return null;
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(Long id) {
        // тоже должна быть проверка на наличие по id
        items.remove(id);
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(i -> (i.getName().contains(text) || i.getDescription().contains(text)) && i.isAvailable() )
                .collect(Collectors.toList());
    }
}
