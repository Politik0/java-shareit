package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @Mock
    private ItemRequestService requestService;
    @InjectMocks
    private ItemRequestController requestController;
    private MockMvc mvc;
    private ItemRequestDto requestDto;
    private ItemRequestDto requestDtoNew;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .build();

        requestDto = ItemRequestDto.builder()
                .id(1L)
                .authorId(1L)
                .created(LocalDateTime.now())
                .description("Descr for request1")
                .build();

        requestDtoNew = ItemRequestDto.builder()
                .description("Descr for request1")
                .build();
    }

    @Test
    void createItemRequest() throws Exception {
        when(requestService.createItemRequest(anyLong(), any()))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDtoNew))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.authorId", is(requestDto.getAuthorId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created").isNotEmpty());
    }

    @Test
    void getItemRequestsByAuthor() throws Exception {
        List<ItemRequestDto> requests = new ArrayList<>();
        requests.add(requestDto);
        when(requestService.getItemRequestsByAuthor(anyLong(), anyInt(), anyInt()))
                .thenReturn(requests);

        mvc.perform(get("/requests?from={from}&size={size}", 0, 10)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].authorId", is(requestDto.getAuthorId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.[0].created").isNotEmpty());
    }

    @Test
    void getAllItemRequests() throws Exception {
        List<ItemRequestDto> requests = new ArrayList<>();
        requests.add(requestDto);
        when(requestService.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(requests);

        mvc.perform(get("/requests/all?from={from}&size={size}", 0, 10)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].authorId", is(requestDto.getAuthorId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.[0].created").isNotEmpty());
    }

    @Test
    void getItemRequestById() throws Exception {
        when(requestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(requestDto);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.authorId", is(requestDto.getAuthorId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created").isNotEmpty());
    }
}