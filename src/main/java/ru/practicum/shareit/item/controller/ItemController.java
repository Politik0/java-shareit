package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
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
    ItemService itemService;
    ItemMapper itemMapper;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        Logger.logRequest(HttpMethod.POST, "/items", itemDto.toString());
        Item item = itemMapper.convertFromDto(itemDto);
        return itemMapper.convertToDto(itemService.addItem(userId, item));
    }

    @GetMapping("{itemId}")
    public ItemDto getItem(@PathVariable long itemId) {
        Logger.logRequest(HttpMethod.GET, "/items/" + itemId, "пусто");
        return itemMapper.convertToDto(itemService.getItemById(itemId));
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        Logger.logRequest(HttpMethod.GET, "/items", "пусто");
        return itemService.getAllItems(userId).stream()
                .map(itemMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        Logger.logRequest(HttpMethod.GET, "/items/search?text=" + text, "пусто");
        return itemService.searchItems(text).stream()
                .map(itemMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        Logger.logRequest(HttpMethod.PATCH, "/items/" + itemId, itemDto.toString());
        Item item = itemMapper.convertFromDto(itemDto);
        return itemMapper.convertToDto(itemService.updateItem(userId, itemId, item));
    }

    @DeleteMapping("{itemId}")
    public void removeItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        Logger.logRequest(HttpMethod.DELETE, "/items/" + itemId, "пусто");
        itemService.removeItem(userId, itemId);
    }
}