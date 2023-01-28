package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
	private long itemId;
	@FutureOrPresent(message = "Дата не должна быть в прошлом")
	@NotNull(message = "Дата не должна быть пустой")
	private LocalDateTime start;
	@Future(message = "Дата не должна быть в прошлом")
	@NotNull(message = "Дата не должна быть пустой")
	private LocalDateTime end;
}
