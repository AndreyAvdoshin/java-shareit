package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Override
    public List<ItemDto> getAllByUserId(Long userId) {
        return null;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        // наверное лучше присвоить все знавение в этом месте
        return null;
    }

    @Override
    public ItemDto getById(Long id, Long userId) {
        return null;
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId) {
        return null;
    }

    @Override
    public void delete(Long id) {
    }

    @Override
    public List<ItemDto> search(String text) {
        return null;
    }
}
