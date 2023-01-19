package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.logger.Logger;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;


    @PostMapping
        // Добавить новый запрос вещи.
    ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestBody @Valid ItemRequestDto itemRequestDto) {
        Logger.logRequest(HttpMethod.POST, "/requests", itemRequestDto.toString());
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
        // Получить список своих запросов вместе с данными об ответах на них.
    List<ItemRequestDto> getItemRequestsByAuthor(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "0")
                                                 @PositiveOrZero(message = "Передаваемые параметры меньше нуля")
                                                 int from,
                                                 @RequestParam(defaultValue = "10", required = false)
                                                 @Positive(message = "Значение size не должно быть отрицательным")
                                                 int size) {
        Logger.logRequest(HttpMethod.GET, "/requests", "no Body");
        return itemRequestService.getItemRequestsByAuthor(userId, from, size);
    }

    @GetMapping("/all")
        // Получить список запросов, созданных другими пользователями.
    List<ItemRequestDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam(defaultValue = "0")
                                            @PositiveOrZero(message = "Передаваемые параметры меньше нуля")
                                            int from,
                                            @RequestParam(defaultValue = "10", required = false)
                                            @Positive(message = "Значение size не должно быть отрицательным")
                                            int size) {
        Logger.logRequest(HttpMethod.GET, String.format("/requests/all?from=%d&size=%d", from, size), "no Body");
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
        // Получить данные об одном конкретном запросе вместе с данными об ответах на него.
    ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        Logger.logRequest(HttpMethod.GET, String.format("/requests/%d", requestId), "no Body");
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
