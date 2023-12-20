package ru.practicum.shareit.item.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllByUserId(@RequestHeader(name = "X-Sharer-User-Id")
                                                            Long userId) {
        if (userId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(itemService.getAllByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                          @RequestBody @Valid ItemDto itemDto) {
        if (userId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
       return ResponseEntity.status(201).body(itemService.create(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                          @PathVariable Long itemId,
                                          @RequestBody ItemDto itemDto) {
        if (userId <= 0 || itemId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(itemService.update(itemDto, userId, itemId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                               @PathVariable Long itemId) {
        if (userId <= 0 || itemId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(itemService.getById(itemId, userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam(required = false) String text) {
        if (!text.isEmpty() && !Pattern.matches("^[\\sа-яА-Яa-zA-Z0-9]+$", text)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else if (text.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(itemService.search(text));
    }

}
