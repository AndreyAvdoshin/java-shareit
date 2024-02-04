package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestClient requestClient;

    @Autowired
    private MockMvc mvc;

    private ItemRequestDto itemRequest;


    @BeforeEach
    void setUp() {
        itemRequest = ItemRequestDto.builder()
                .id(1L)
                .description("Очень нужна эта вещь")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldGet201WhenCreateRequest() throws Exception {
        String request = mapper.writeValueAsString(itemRequest);

        when(requestClient.createItemRequest(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(new ResponseEntity<>(request, HttpStatus.CREATED));

        String content = mvc.perform(post("/requests")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(request, content);
    }

    @Test
    void shouldGet200WhenGetRequestsByUserId() throws Exception {
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(requestClient).getRequests(anyLong());
    }

    @Test
    void shouldGet200WhenGetAllRequests() throws Exception {
        mvc.perform(get("/requests/all")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(1))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(requestClient).getAllRequests(anyInt(), anyInt(), anyLong());
    }

    @Test
    void shouldGet200WhenGetRequestById() throws Exception {
        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(requestClient).getRequestById(anyLong(), anyLong());
    }

}