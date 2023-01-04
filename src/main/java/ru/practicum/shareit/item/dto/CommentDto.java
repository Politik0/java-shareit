package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    long id;
    @NotBlank(message = "Комментарий не должен быть пустым.")
    String text;
    String authorName;
    LocalDateTime created;
}
