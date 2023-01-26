package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.logger.Logger;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        Logger.logRequest(HttpMethod.POST, "/items", itemDto.toString());
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        Logger.logRequest(HttpMethod.PATCH, "/items/" + itemId, itemDto.toString());
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        Logger.logRequest(HttpMethod.GET, "/items/" + itemId, "пусто");
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping     // Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой
    public List<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(defaultValue = "0")
                                         @PositiveOrZero(message = "Передаваемые параметры меньше нуля")
                                         int from,
                                         @RequestParam(defaultValue = "10", required = false)
                                         @Positive(message = "Значение size не должно быть отрицательным")
                                         int size) {
        Logger.logRequest(HttpMethod.GET, "/items", "пусто");
        return itemService.getAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text, @RequestParam(defaultValue = "0")
                                     @PositiveOrZero(message = "Передаваемые параметры меньше нуля")
                                     int from,
                                     @RequestParam(defaultValue = "10", required = false)
                                     @Positive(message = "Значение size не должно быть отрицательным")
                                     int size) {
        Logger.logRequest(HttpMethod.GET, "/items/search?text=" + text, "пусто");
        return itemService.searchItems(text, from, size);
    }

    @DeleteMapping("{itemId}")
    public void removeItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        Logger.logRequest(HttpMethod.DELETE, "/items/" + itemId, "пусто");
        itemService.removeItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                              @RequestBody @Valid CommentDto commentDto) {
        Logger.logRequest(HttpMethod.POST, "/items/" + itemId + "/comment", commentDto.toString());
        return itemService.addComment(userId, itemId, commentDto);
    }
}