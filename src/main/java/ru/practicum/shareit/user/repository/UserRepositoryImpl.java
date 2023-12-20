package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long countId = 0L;

    @Override
    public List<User> getAllUsers() {
        log.info("Получение всех пользователей");
        return new ArrayList<>(users.values());
    }
    @Override
    public User create(User user) {
        user.setId(++countId);
        users.put(user.getId(), user);
        log.info("Создан пользователь - {}", user);
        return user;
    }

    @Override
    public User getById(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь по id - " + userId + " не найден");
        }
        log.info("Получен пользователь по id - {}", userId);
        return user;
    }

    @Override
    public User update(User user) {
        log.info("Пользователь {} обновлен", user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        log.info("Пользователь по id - {} удален", userId);
        users.remove(userId);
    }
}
