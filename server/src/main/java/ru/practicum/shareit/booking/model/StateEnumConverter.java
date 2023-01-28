package ru.practicum.shareit.booking.model;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StateEnumConverter implements Converter<String, State> {

    @Override
    public State convert(String source) {
        try {
            return State.valueOf(source);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Unknown state: %S", source));
        }
    }
}
