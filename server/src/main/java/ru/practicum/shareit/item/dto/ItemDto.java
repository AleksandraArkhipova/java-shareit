package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;

    String name;

    String description;

    Boolean available;

    User owner;

    ShortBookingDto lastBooking;

    ShortBookingDto nextBooking;

    List<CommentDto> comments;

    Long requestId;
}