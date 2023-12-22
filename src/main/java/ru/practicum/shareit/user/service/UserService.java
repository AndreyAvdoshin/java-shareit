package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto create(UserDto userDto);

    UserDto getById(Long userId);

    UserDto update(UserDto uSerDto, Long userId);

    void delete(Long userId);
}
