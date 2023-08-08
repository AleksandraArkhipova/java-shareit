package ru.practicum.shareit.exception;

import lombok.Data;

@Data
public class FieldError {
    private final String field;
    private final String description;
}
