package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    List<User> getAllUsers();

    User create(User user);

    User getById(Long userId);

    User update(User user);

    void delete(Long userId);
}
