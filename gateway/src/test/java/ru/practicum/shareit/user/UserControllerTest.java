package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserClient userClient;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto;

    @BeforeEach
    void SetUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("User")
                .email("user@user.ru")
                .build();
    }

    @Test
    void shouldGet201WhenCreateUser() throws Exception {
        String user = mapper.writeValueAsString(userDto);

        when(userClient.createUser(any(UserDto.class))).thenReturn(new ResponseEntity<>(user, HttpStatus.CREATED));

        String content = mvc.perform(post("/users")
                        .content(user)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(user, content);
    }

    @Test
    void shouldGet200WhenUpdateUser() throws Exception {
        String user = mapper.writeValueAsString(userDto);

        when(userClient.updateUser(any(UserDto.class), anyLong()))
                .thenReturn(new ResponseEntity<>(user, HttpStatus.OK));

        String content = mvc.perform(patch("/users/{id}", 1)
                        .content(user)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(user, content);
    }

    @Test
    void shouldGet200WhenGetUserById() throws Exception {
        mvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk());

        verify(userClient).getUserById(anyLong());
    }

    @Test
    void shouldGet200WhenDeleteUser() throws Exception {
        mvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isOk());

        verify(userClient).delete(anyLong());
    }

    @Test
    void shouldGet200WhenGetAllUsers() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userClient).getAllUsers();
    }

}