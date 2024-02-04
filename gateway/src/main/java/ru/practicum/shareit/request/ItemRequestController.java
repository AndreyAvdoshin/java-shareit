package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader(name = "X-Sharer-User-Id") @Positive @NotNull Long userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Запрос создания запроса пользователем id - {}", userId);
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUserId(@RequestHeader(name = "X-Sharer-User-Id")
                                                        @Positive @NotNull Long userId) {
        log.info("Запрос получения всех запросов пользователем id - {}", userId);
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(name = "X-Sharer-User-Id") @Positive @NotNull Long userId,
                                         @PathVariable Long requestId) {
        log.info("Запрос получения запроса по id - {} пользователем - {}", requestId, userId);
        return itemRequestClient.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(name = "X-Sharer-User-Id") @Positive @NotNull Long userId,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero @NotNull Integer from,
                                               @RequestParam(defaultValue = "10") @Positive @NotNull Integer size) {
        log.info("Запрос всех запросов со страницы - {} количество - {} пользователем - {}", from, size, userId);
        return itemRequestClient.getAllRequests(from, size, userId);
    }
}
