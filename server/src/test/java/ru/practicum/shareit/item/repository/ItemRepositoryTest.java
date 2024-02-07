package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private Item item;
    private Item otherItem;
    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {

        user = User.builder()
                .name("User")
                .email("user@user.com")
                .build();
        userRepository.save(user);

        itemRequest = ItemRequest.builder()
                .description("Очень нужна эта вещь")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .name("Вещь")
                .description("Очень хорошая вещь")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();

        otherItem = Item.builder()
                .name("Компьютер")
                .description("Великолепный персональный компьютер")
                .available(true)
                .owner(user)
                .build();

        itemRepository.save(item);
        itemRepository.save(otherItem);
        itemRequestRepository.save(itemRequest);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void shouldGetItemsByText() {
        List<Item> items = itemRepository.search("очень", PageRequest.of(0, 10));

        assertEquals(items.size(), 1);
        assertEquals(items, List.of(item));
    }

    @Test
    void shouldGetAllItemWithRequests() {
        List<Item> items = itemRepository.getAllWithRequests();

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getRequest(), itemRequest);
    }


}