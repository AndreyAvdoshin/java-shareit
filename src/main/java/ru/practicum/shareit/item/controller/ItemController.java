package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
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
    public List<ItemOutputDto> getAllByUserId(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "0", required = false) int from,
                                              @RequestParam(defaultValue = "10", required = false) int size) {
        log.info("Запрос всех вещей пользователя по id - {}, со страницы - {}, количеством - {}", userId, from, size);
        return itemService.getAllByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemWithRequestDto create(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                     @RequestBody @Valid ItemWithRequestDto itemWithRequestDto) {
        log.info("Запрос создания вещи - {}", itemWithRequestDto);
        return itemService.create(itemWithRequestDto, userId);
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
    public List<ItemDto> search(@RequestParam(required = false) String text,
                                @RequestParam(defaultValue = "0", required = false) int from,
                                @RequestParam(defaultValue = "10", required = false) int size) {
        log.info("Запрос поиска вещи по строке - {} со страницы - {} количеством - {}", text, from, size);
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId,
                                    @RequestBody @Valid CommentDto commentDto) {
        log.info("Запрос создания комментария к вещи по id - {} автором - {} : {}", itemId, userId, commentDto);
        return itemService.createComment(userId, itemId, commentDto);
    }
}
