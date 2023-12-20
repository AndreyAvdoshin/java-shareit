package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UniqueViolatedException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

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
        if (checkAlreadyRegisteredUser(user.getEmail())) {
            throw new UniqueViolatedException("Пользователь с email: " + user.getEmail() + " уже зарегистрирован");
        }
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
        User user = UserMapper.toUser(userDto);
        User replasedUser = userRepository.getById(userId);
        boolean exists = checkAlreadyRegisteredUser(user.getEmail());
        if (user.getEmail() != null) {
            if (checkAlreadyRegisteredUser(user.getEmail()) &&
                    !user.getEmail().equalsIgnoreCase(replasedUser.getEmail())) {
                throw new UniqueViolatedException("Пользователь с email: " + user.getEmail() + " уже зарегистрирован");
            }
            replasedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            replasedUser.setName(user.getName());
        }
        user = userRepository.update(replasedUser);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(Long userId) {
        userRepository.delete(userId);
    }

    private boolean checkAlreadyRegisteredUser(String email) {
        return getAllUsers().stream()
                .anyMatch(it -> it.getEmail().equalsIgnoreCase(email));
    }
}
