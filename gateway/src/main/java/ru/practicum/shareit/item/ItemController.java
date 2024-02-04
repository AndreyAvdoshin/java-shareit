package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.regex.Pattern;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader(name = "X-Sharer-User-Id")
                                                     @Positive @NotNull Long userId,
                                                 @RequestParam(defaultValue = "0")
                                                 @PositiveOrZero @NotNull Integer from,
                                                 @RequestParam(defaultValue = "10") @Positive @NotNull Integer size) {
        log.info("Запрос всех вещей пользователя по id - {}, со страницы - {}, количеством - {}", userId, from, size);
        return itemClient.getAllByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader(name = "X-Sharer-User-Id") @Positive @NotNull Long userId,
                                         @RequestBody @Valid ItemWithRequestDto itemWithRequestDto) {
        log.info("Запрос создания вещи - {}", itemWithRequestDto);
        return itemClient.createItem(itemWithRequestDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(name = "X-Sharer-User-Id") @Positive @NotNull Long userId,
                                         @PathVariable @Positive @NotNull Long itemId,
                                         @RequestBody ItemDto itemDto) {
        log.info("Запрос обновления вещи - {} по пользователю id - {}", itemDto, itemId);
        return itemClient.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(name = "X-Sharer-User-Id") @Positive @NotNull Long userId,
                                              @PathVariable @Positive @NotNull Long itemId) {
        log.info("Запрос вещи пользователем - {} по id - {}", userId, itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(name = "X-Sharer-User-Id") @Positive @NotNull Long userId,
                                         @RequestParam(required = false) String text,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero @NotNull Integer from,
                                         @RequestParam(defaultValue = "10") @Positive @NotNull Integer size) {
        if (!text.isEmpty() && !Pattern.matches("^[\\sа-яА-Яa-zA-Z0-9]+$", text)) {
            throw new IncorrectParameterException("text");
        } else if (text.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        log.info("Запрос поиска вещи по строке - {} со страницы - {} количеством - {}", text, from, size);
        return itemClient.search(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(name = "X-Sharer-User-Id")
                                                    @Positive @NotNull Long userId,
                                                @PathVariable @Positive @NotNull Long itemId,
                                                @RequestBody @Valid CommentDto commentDto) {
        log.info("Запрос создания комментария к вещи по id - {} автором - {} : {}", itemId, userId, commentDto);
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
