package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemServiceImpl(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public List<ItemDto> getAllByUserId(Long userId) {
        checkUserIfExists(userId);
        return itemRepository.getAllByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        checkUserIfExists(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        return ItemMapper.toItemDto(itemRepository.create(item));
    }

    @Override
    public ItemDto getById(Long id, Long userId) {
        checkUserIfExists(userId);
        Item item = itemRepository.getById(id);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId, Long id) {
        checkUserIfExists(userId);
        Item updatedItem = itemRepository.getById(id);
        if (!updatedItem.getOwnerId().equals(userId)) {
            throw new NotOwnerException("Запрещено редактировать не свою вещь");
        }
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        updatedItem = itemRepository.update(updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.search(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkUserIfExists(Long userId) {
        userService.getById(userId);
    }
}
