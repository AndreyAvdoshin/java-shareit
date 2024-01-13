package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto create(UserDto userDto);

    UserDto getById(Long userId);

    UserDto update(UserDto uSerDto, Long userId);

    void delete(Long userId);

    User returnUserIfExists(Long userId);

    void checkUserIfExists(Long userId);
}
