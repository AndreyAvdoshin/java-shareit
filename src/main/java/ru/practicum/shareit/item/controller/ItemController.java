package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDto> getAllByUserId(@RequestHeader(name = "X-Sharer-User-Id")
                                                            Long userId) {
        log.info("Запрос всех вещей пользователя по id - {}", userId);
        return itemService.getAllByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                          @RequestBody @Valid ItemDto itemDto) {
        log.info("Запрос создания вещи - {}", itemDto);
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                          @PathVariable Long itemId,
                                          @RequestBody ItemDto itemDto) {
        log.info("Запрос обновления вещи - {} по пользователю id - {}", itemDto, itemId);
        return itemService.update(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                               @PathVariable Long itemId) {
        log.info("Запрос вещи пользователем - {} по id - {}", userId, itemId);
        return itemService.getById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(required = false) String text) {
        log.info("Запрос поиска вещи по строке - {}", text);
        return itemService.search(text);
    }
}
