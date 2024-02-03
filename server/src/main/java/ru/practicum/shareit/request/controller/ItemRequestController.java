package ru.practicum.shareit.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Запрос создания запроса пользователем id - {}", userId);
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByUserId(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос получения всех запросов пользователем id - {}", userId);
        return itemRequestService.getRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                         @PathVariable Long requestId) {
        log.info("Запрос получения запроса по id - {} пользователем - {}", requestId, userId);
        return itemRequestService.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Запрос всех запросов со страницы - {} количество - {} пользователем - {}", from, size, userId);
        return itemRequestService.getAllRequests(from, size, userId);
    }
}
