package ru.practicum.shareit.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toComment(CreateCommentDto commentDto);

    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDto toCommentDto(Comment comment);
}