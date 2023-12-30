package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ItemDto> getAllByUserId(Long userId) {
        if (userId <= 0) {
            throw new IncorrectParameterException("userId");
        }
        checkUserIfExists(userId);
        return itemRepository.findByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        if (userId <= 0) {
            throw new IncorrectParameterException("userId");
        }
        checkUserIfExists(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userRepository.findById(userId).get());
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getById(Long itemId, Long userId) {
        if (userId <= 0) {
            throw new IncorrectParameterException("userId");
        } else if (itemId <= 0) {
            throw new IncorrectParameterException("itemId");
        }

        checkUserIfExists(userId);
        Item item = returnItemIfExists(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId, Long itemId) {
        if (userId <= 0) {
            throw new IncorrectParameterException("userId");
        } else if (itemId <= 0) {
            throw new IncorrectParameterException("itemId");
        }

        checkUserIfExists(userId);
        Item updatedItem = returnItemIfExists(itemId);
        if (!updatedItem.getOwner().getId().equals(userId)) {
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
        updatedItem = itemRepository.save(updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (!text.isEmpty() && !Pattern.matches("^[\\sа-яА-Яa-zA-Z0-9]+$", text)) {
            throw new IncorrectParameterException("text");
        } else if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return null;
//        return itemRepository.search(text.toLowerCase()).stream()
//                .map(ItemMapper::toItemDto)
//                .collect(Collectors.toList());
    }

    private Item returnItemIfExists(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь по id - " + itemId + " не найдена"));
    }

    private void checkUserIfExists(Long userId) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new NotFoundException("Пользователь по id - " + userId + " не найден");
        }
    }
}
