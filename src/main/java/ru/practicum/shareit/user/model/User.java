package ru.practicum.shareit.user.model;

import lombok.Data;
import java.util.Set;

@Data
public class User {
    private long id;
    private String email;
    private String name;
    private Set<Long> items;
}