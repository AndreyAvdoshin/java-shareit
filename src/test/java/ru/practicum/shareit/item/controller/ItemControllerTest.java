package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ItemController itemController;
    @Autowired
    UserController userController;

    ItemDto itemDto;
    ItemDto newItemDto;
    UserDto userDto;
    UserDto newUserDto;

    @BeforeEach
    public void setUp() {
        itemDto = ItemDto.builder()
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .build();

        newItemDto = ItemDto.builder()
                .name("Дрель2")
                .description("Другая дрель")
                .available(true)
                .build();

        userDto = UserDto.builder()
                .name("user")
                .email("user@user.com")
                .build();

        newUserDto = UserDto.builder()
                .name("user2")
                .email("user2@user.com")
                .build();
    }

    @Test
    void getAllByUserId() throws Exception {
        userDto = userController.create(userDto).getBody();
        itemController.create(userDto.getId(), itemDto).getBody();
        itemController.create(userDto.getId(), newItemDto).getBody();

        mockMvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].description").value("Простая дрель"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Дрель2"))
                .andExpect(jsonPath("$[1].description").value("Другая дрель"))
                .andExpect(jsonPath("$[1].available").value(true));

    }

    @Test
    void shouldCreateItemAndGet201() throws Exception {
        userDto = userController.create(userDto).getBody();

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Простая дрель"));
    }

    @Test
    void shouldGet500WhenEmptyUser() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldGet404WhenUserNotFound() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateItemAndGet200() throws Exception {
        userDto = userController.create(userDto).getBody();
        itemDto = itemController.create(userDto.getId(), itemDto).getBody();

        itemDto.setName("Дрель+");
        itemDto.setDescription("Аккумуляторная дрель");
        itemDto.setAvailable(false);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId())
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель+"))
                .andExpect(jsonPath("$.description").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$.available").value("false"));
    }

    @Test
    void shouldGet403WhenUpdateItemWrongUser() throws Exception {
        userDto = userController.create(userDto).getBody();
        userController.create(newUserDto).getBody();
        itemDto = itemController.create(userDto.getId(), itemDto).getBody();

        itemDto.setName("Дрель+");
        itemDto.setDescription("Аккумуляторная дрель");

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetItemBySearchRequest() throws Exception {
        userDto = userController.create(userDto).getBody();
        itemDto = itemController.create(userDto.getId(), itemDto).getBody();

        mockMvc.perform(get("/items/search?text=дРелЬ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].description").value("Простая дрель"))
                .andExpect(jsonPath("$[0].available").value(true));
    }
}