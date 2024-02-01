package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.exception.UniqueViolatedException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"db.name=test"})
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private User user;
    private User owner;
    private UserDto userDto;
    private UserDto ownerDto;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .name("User")
                .email("user@user.ru")
                .build();

        owner = User.builder()
                .id(2L)
                .name("Owner")
                .email("owner@user.ru")
                .build();

        userDto = UserMapper.toUserDto(user);
        ownerDto = UserMapper.toUserDto(owner);
    }

    @Test
    void shouldGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user, owner));

        List<UserDto> users = userService.getAllUsers();

        assertEquals(users, List.of(userDto, ownerDto));
    }

    @Test
    void shouldCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto outputUser = userService.create(userDto);

        assertEquals(outputUser, userDto);
    }

    @Test
    void shouldGetUserById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto outputUser = userService.getById(user.getId());

        assertEquals(outputUser, userDto);
    }

    @Test
    void shouldUpdateUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(owner);
        UserDto outputUser = userService.update(ownerDto, owner.getId());

        assertEquals(outputUser, ownerDto);
    }

    @Test
    void shouldDeleteUser() {
        doNothing().when(userRepository).deleteById(anyLong());

        userService.delete(user.getId());

        verify(userRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void shouldThrowUniqueViolatedExceptionWhenUserNotUnique() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        assertThrows(UniqueViolatedException.class,
                () -> userService.update(userDto, user.getId()));
    }

}