package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private Long countId = 0L;

    @Override
    public List<Item> getAllByUserId(Long userId) {
        log.info("Запрос всех вещей пользователя - {}", userId);
        return items.values().stream()
                .filter(i -> i.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item create(Item item) {
        item.setId(++countId);
        items.put(item.getId(), item);
        log.info("Создана вещь - {}", item);
        return item;
    }

    @Override
    public Item getById(Long id) {
        log.info("Получение вещи по id - {}", id);
        return items.get(id);
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        log.info("Обновлена вещь - {}", item);
        return item;
    }

    @Override
    public List<Item> search(String text) {
        log.info("Поиск вещь по строке - {}", text);
        return items.values().stream()
                .filter(i -> (i.getName().toLowerCase().contains(text) ||
                        i.getDescription().toLowerCase().contains(text)) && i.isAvailable())
                .collect(Collectors.toList());
    }
}
