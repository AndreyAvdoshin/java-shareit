package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Запрос всех пользователей");
        return userClient.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto userDto) {
        log.info("Запрос создания пользователя - {}", userDto);
        return userClient.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable @Positive @NotNull Long userId) {
        log.info("Запрос пользователя по id- {}", userId);
        return userClient.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable @Positive @NotNull Long userId, @RequestBody UserDto userDto) {
        log.info("Запрос обновления пользователя по id - {}", userId);
        return userClient.updateUser(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable @Positive @NotNull Long userId) {
        log.info("Запрос удаления пользователя по id - {}", userId);
        return userClient.delete(userId);
    }
}
