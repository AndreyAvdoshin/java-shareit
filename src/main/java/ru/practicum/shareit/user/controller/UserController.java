package ru.practicum.shareit.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.ok(userService.create(userDto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getById(@PathVariable Long userId) {
        if (userId <= 0) {
            return  ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.getById(userId));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        if (userId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.update(userDto, userId));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }

}
