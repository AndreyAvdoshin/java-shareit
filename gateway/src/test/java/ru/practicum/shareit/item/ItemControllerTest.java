package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void SetUp() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Вещь")
                .description("Очень классная вещь")
                .available(true)
                .requestId(1L)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("Классная вещица")
                .authorName("user")
                .created(LocalDateTime.now()).build();
    }

    @Test
    public void shouldReturn201WhenCreateItem() throws Exception {
        String item = mapper.writeValueAsString(itemDto);

        when(itemClient.createItem(any(ItemDto.class), anyLong())).thenReturn(new ResponseEntity<>(item, HttpStatus.CREATED));

        String content = mvc.perform(post("/items")
                        .content(item)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(item, content);
    }

    @Test
    void shouldReturn200WhenUpdateItem() throws Exception {
        String item = mapper.writeValueAsString(itemDto);

        when(itemClient.updateItem(any(ItemDto.class), anyLong(), anyLong())).thenReturn(new ResponseEntity<>(item, HttpStatus.OK));

        String content = mvc.perform(patch("/items/{Id}", 1)
                        .content(item)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(item, content);
    }

    @Test
    void shouldReturn200WhenGetItemById() throws Exception {
        mvc.perform(get("/items/{Id}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemClient).getItemById(anyLong(), anyLong());
    }

    @Test
    void getAll_shouldReturnStatusOk() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemClient).getAllByUserId(anyLong(), anyInt(), anyInt());
    }

    @Test
    void search_shouldReturnStatusOk() throws Exception {
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "вещь"))
                .andExpect(status().isOk());

        verify(itemClient).search(anyString(), anyLong(), anyInt(), anyInt());
    }

    @Test
    void addComment() throws Exception {
        String comment = mapper.writeValueAsString(commentDto);

        when(itemClient.createComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(new ResponseEntity<>(comment, HttpStatus.OK));

        String content = mvc.perform(post("/items/{id}/comment", 1)
                        .content(comment)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(comment, content);

        verify(itemClient).createComment(anyLong(), anyLong(), any(CommentDto.class));
    }

}