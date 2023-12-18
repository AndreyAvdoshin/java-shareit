package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user = userRepository.create(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getById(Long userId) {
        User user = userRepository.getById(userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        User user = userRepository.getById(userId); // Првоерка на наличие пользователя
        user = UserMapper.toUser(userDto);
        user.setId(userId);
        user = userRepository.update(user);
        log.info("Обновленный пользователь: {}", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(Long userId) {
        userRepository.delete(userId);
    }

}
