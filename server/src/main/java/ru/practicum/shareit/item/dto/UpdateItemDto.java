package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemDto {
    String name;
    String description;
    Boolean available;
}