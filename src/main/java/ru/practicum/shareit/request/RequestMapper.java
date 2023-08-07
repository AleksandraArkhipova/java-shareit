package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    Request toRequest(CreateRequestDto dto);

    RequestDto toRequestDto(Request request);
}