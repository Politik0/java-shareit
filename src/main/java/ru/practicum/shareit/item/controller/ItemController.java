package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.logger.Logger;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    public final static String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addItem(@RequestHeader(X_SHARER_USER_ID) long userId, @Valid @RequestBody ItemDto itemDto) {
        Logger.logRequest(HttpMethod.POST, "/items", itemDto.toString());
        Item item = itemMapper.convertFromDto(itemDto);
        return itemMapper.convertToDto(itemService.addItem(userId, item));
    }

    @GetMapping("{itemId}")
    public ItemDto getItem(@PathVariable long itemId, @RequestHeader(X_SHARER_USER_ID) long userId) {
        Logger.logRequest(HttpMethod.GET, "/items/" + itemId, "пусто");
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping     // Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой
    public List<ItemDto> getAllItems(@RequestHeader(X_SHARER_USER_ID) long userId) {
        Logger.logRequest(HttpMethod.GET, "/items", "пусто");
        return itemService.getAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        Logger.logRequest(HttpMethod.GET, "/items/search?text=" + text, "пусто");
        return itemService.searchItems(text).stream()
                .map(itemMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        Logger.logRequest(HttpMethod.PATCH, "/items/" + itemId, itemDto.toString());
        Item item = itemMapper.convertFromDto(itemDto);
        return itemMapper.convertToDto(itemService.updateItem(userId, itemId, item));
    }

    @DeleteMapping("{itemId}")
    public void removeItem(@RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long itemId) {
        Logger.logRequest(HttpMethod.DELETE, "/items/" + itemId, "пусто");
        itemService.removeItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable long itemId,
                              @RequestBody @Valid CommentDto commentDto) {
        Logger.logRequest(HttpMethod.POST, "/items/" + itemId + "/comment", commentDto.toString());
        Comment comment = commentMapper.convertFromDto(commentDto);
        return commentMapper.convertToDto(itemService.addComment(userId, itemId, comment));
    }
}