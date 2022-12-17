package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {
    private long id;
    @NotBlank(message = "Поле с именем не должно быть пустым.")
    private String name;
    @NotBlank(message = "Поле с описанием не должно быть пустым.")
    private String description;
    @NotNull(message = "Поле Available не должно быть пустым.")
    private Boolean available;
}