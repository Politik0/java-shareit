package ru.practicum.shareit.item.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    private final ModelMapper modelMapper;

    ItemMapper() {
        modelMapper = new ModelMapper();
        modelMapper.createTypeMap(ItemDto.class, Item.class)
                .addMappings(modelMapper -> modelMapper.skip(Item::setUserId));
    }

    public ItemDto convertToDto(Item item) {
        return modelMapper.map(item, ItemDto.class);
    }

    public Item convertFromDto(ItemDto itemDto) {
        return modelMapper.map(itemDto, Item.class);
    }
}