package ru.practicum.shareit.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.comment.CommentJpaRepository;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentJpaRepository repo;
}
