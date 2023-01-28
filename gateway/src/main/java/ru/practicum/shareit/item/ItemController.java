package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@Controller
@RequestMapping("/items")
@Slf4j
@AllArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Updating item {}, itemId={}, userId={}", itemDto, itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting item, itemId={}, userId={}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping     // Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(defaultValue = "0")
                                         @PositiveOrZero(message = "Передаваемые параметры меньше нуля")
                                         int from,
                                         @RequestParam(defaultValue = "10", required = false)
                                         @Positive(message = "Значение size не должно быть отрицательным")
                                         int size) {
        log.info("Getting all items by owner, ownerId={}, from={}, size={}", userId, from, size);
        return itemClient.getItemsByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam String text, @RequestParam(defaultValue = "0")
                                                  @PositiveOrZero(message = "Передаваемые параметры меньше нуля")
                                                  int from,
                                              @RequestParam(defaultValue = "10", required = false)
                                                  @Positive(message = "Значение size не должно быть отрицательным")
                                                  int size) {
        log.info("Searching items with text={}, from={}, size={}", text, from, size);
        return itemClient.searchItems(text, from, size);
    }

    @DeleteMapping("{itemId}")
    public ResponseEntity<Object> removeItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Deleting item, itemId={}, userId={}", itemId, userId);
        return itemClient.removeItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                              @RequestBody @Valid CommentDto commentDto) {
        log.info("Posting comment {} for item, userId={}, itemId={}", commentDto, userId, itemId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}