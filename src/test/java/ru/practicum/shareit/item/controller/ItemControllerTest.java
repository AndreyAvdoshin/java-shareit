package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {"db.name=test"})
@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private BookingShortDto bookingShortDto;
    private ItemWithRequestDto itemWithRequestDto;
    private ItemOutputDto itemOutputDto;
    private CommentDto commentDto;

    @BeforeEach
    void startUp() {
        user = User.builder()
                .id(1L)
                .email("user@email.ru")
                .name("User")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Вещь")
                .description("Очень классная вещь")
                .available(true)
                .owner(user)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Другая вещь")
                .description("Эта вещь ешё класснее!")
                .available(true)
                .build();

        bookingShortDto = BookingShortDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2)).build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("Классная вещица")
                .authorName("user")
                .created(LocalDateTime.now()).build();

        itemOutputDto = new ItemOutputDto(item, bookingShortDto, bookingShortDto);
        itemWithRequestDto = ItemMapper.toItemWithRequestDto(item);
    }

    @Test
    void shouldGetAllItemsByUserId() throws Exception {
        when(itemService.getAllByUserId(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemOutputDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemOutputDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemOutputDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemOutputDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemOutputDto.getAvailable())));

        verify(itemService, times(1)).getAllByUserId(anyLong(), anyInt(), anyInt());
    }

    @Test
    void shouldCreateItem() throws Exception {
        when(itemService.create(any(ItemWithRequestDto.class), anyLong())).thenReturn(itemWithRequestDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemWithRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemWithRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemWithRequestDto.getName())))
                .andExpect(jsonPath("$.description", is(itemWithRequestDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemWithRequestDto.getAvailable())));

        verify(itemService, times(1)).create(any(), anyLong());
    }

    @Test
    void shouldUpdateItem() throws Exception {
        when(itemService.update(any(ItemDto.class), any(), any())).thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", 1L)
                .content(mapper.writeValueAsString(itemDto))
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService, times(1)).update(any(), anyLong(), anyLong());
    }

    @Test
    void shouldGetItemById() throws Exception {
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService, times(1)).getById(anyLong(), anyLong());
    }

    @Test
    void shouldGetItemsBySearchText() throws Exception {
        when(itemService.search(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "вещь"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())));

        verify(itemService, times(1)).search(anyString(), anyInt(), anyInt());
    }

    @Test
    void shouldCreateComment() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().toString())));

        verify(itemService, times(1)).createComment(anyLong(), anyLong(), any());
    }

}