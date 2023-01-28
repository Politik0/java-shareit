package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;
    @Email(message = "Поле email заполненно некорректно. Проверьте формат.")
    @NotBlank(message = "Поле email не должно быть пустым.")
    private String email;
    @NotBlank(message = "Поле name не должно быть пустым.")
    private String name;
}