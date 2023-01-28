package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.logger.Logger;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto itemDto) {
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
                                         @RequestParam int from, @RequestParam int size) {
        Logger.logRequest(HttpMethod.GET, "/items", "пусто");
        return itemService.getAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text, @RequestParam int from, @RequestParam int size) {
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
                                 @RequestBody CommentDto commentDto) {
        Logger.logRequest(HttpMethod.POST, "/items/" + itemId + "/comment", commentDto.toString());
        return itemService.addComment(userId, itemId, commentDto);
    }
}