package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long countId = 0L;


    @Override
    public User create(User user) {
        if (isAlreadyRegisteredUser(user.getEmail())) {
            return null; // Необходимо написать ошибку валидации почты
        }
        user.setId(++countId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getById(Long userId) {
        return users.get(userId);
    }

    @Override
    public User update(User user) {
        User replasedUser = users.get(user.getId());
        if (replasedUser == null) {
            // надо написать исключение, для поиска объектов
           return null;
        }
        if (isAlreadyRegisteredUser(user.getEmail())) {
            return null; // Необходимо написать ошибку валидации почты
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }

    private boolean isAlreadyRegisteredUser(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }
}
