package ru.practicum.shareit.request.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Component
public class ItemRequestMapper {
    private final ModelMapper modelMapper;

    public ItemRequestMapper() {
        modelMapper = new ModelMapper();
    }

    public ItemRequestDto convertToDto(ItemRequest itemRequest) {
        return modelMapper.map(itemRequest, ItemRequestDto.class);
    }

    public ItemRequest convertFromDto(ItemRequestDto itemRequestDto) {
        return modelMapper.map(itemRequestDto, ItemRequest.class);
    }
}
