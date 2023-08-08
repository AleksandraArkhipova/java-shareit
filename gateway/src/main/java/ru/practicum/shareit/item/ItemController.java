package ru.yandex.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.shareit.item.dto.CreateCommentDto;
import ru.yandex.practicum.shareit.item.dto.CreateItemDto;
import ru.yandex.practicum.shareit.item.dto.UpdateItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/items")
public class ItemController {
    static final String USER_ID_HEADER = "X-Sharer-User-Id";
    ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getByUserId(
            @RequestHeader(name = USER_ID_HEADER) long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "20") Integer size
    ) {
        return itemClient.getByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestParam String text,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "20") Integer size
    ) {
        if (text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return itemClient.search(text, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable long id, @RequestHeader(name = USER_ID_HEADER) long userId) {
        return itemClient.getById(id, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(
            @RequestHeader(name = USER_ID_HEADER) long userId,
            @Valid @RequestBody CreateItemDto dto
    ) {
        return itemClient.create(userId, dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @PathVariable long id,
            @RequestHeader(name = USER_ID_HEADER) long userId,
            @Valid @RequestBody UpdateItemDto dto
    ) {
        return itemClient.update(id, userId, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable long id) {
        return itemClient.delete(id);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> comment(
            @PathVariable long id,
            @RequestHeader(name = USER_ID_HEADER) long userId,
            @Valid @RequestBody CreateCommentDto dto
    ) {
        return itemClient.comment(id, userId, dto);
    }
}