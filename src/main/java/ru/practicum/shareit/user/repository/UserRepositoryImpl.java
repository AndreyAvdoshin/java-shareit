package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UniqueViolatedException;
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
        return new ArrayList<>(users.values());
    }
    @Override
    public User create(User user) {
        checkAlreadyRegisteredUser(user.getEmail(), user.getId());
        user.setId(++countId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getById(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь по id - " + userId + " не найден");
        }
        log.info("Получен пользователь {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        User replasedUser = users.get(user.getId());
        if (replasedUser == null) {
            throw new NotFoundException("Пользователь по id - " + user.getId() + " не найден");
        }
        checkAlreadyRegisteredUser(user.getEmail(), user.getId());
        if (user.getName() != null) {
            replasedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            replasedUser.setEmail(user.getEmail());
        }
        log.info("Пользователь {} обновлен", user);
        users.put(replasedUser.getId(), replasedUser);
        return replasedUser;
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }

    private void checkAlreadyRegisteredUser(String email, Long id) {
         boolean exist = users.values().stream()
                .anyMatch(it -> it.getEmail().equalsIgnoreCase(email) && !it.getId().equals(id));
         if (exist) {
             throw new UniqueViolatedException("Пользователь с email: " + email + " уже зарегистрирован");
         }
    }
}
