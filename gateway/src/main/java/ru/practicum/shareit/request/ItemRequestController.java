package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@Controller
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;


    @PostMapping
    // Добавить новый запрос вещи.
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Creating item request {}, userId={}", itemRequestDto, userId);
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    // Получить список своих запросов вместе с данными об ответах на них.
    public ResponseEntity<Object> getItemRequestsByAuthor(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @RequestParam(defaultValue = "0")
                                                          @PositiveOrZero(message = "Передаваемые параметры меньше нуля")
                                                          int from,
                                                          @RequestParam(defaultValue = "10", required = false)
                                                          @Positive(message = "Значение size не должно быть отрицательным")
                                                          int size) {
        log.info("Getting all author's item requests, authorId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getItemRequestsByAuthor(userId, from, size);
    }

    @GetMapping("/all")
    // Получить список запросов, созданных другими пользователями.
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(defaultValue = "0")
                                                     @PositiveOrZero(message = "Передаваемые параметры меньше нуля")
                                                     int from,
                                                     @RequestParam(defaultValue = "10", required = false)
                                                     @Positive(message = "Значение size не должно быть отрицательным")
                                                     int size) {
        log.info("Getting all item requests, userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    // Получить данные об одном конкретном запросе вместе с данными об ответах на него.
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        log.info("Getting item request, requestId={}, userId={}", requestId, userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
