package ru.yandex.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.yandex.practicum.shareit.exception.client.BaseClient;
import ru.yandex.practicum.shareit.item.dto.CreateCommentDto;
import ru.yandex.practicum.shareit.item.dto.CreateItemDto;
import ru.yandex.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getByUserId(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> search(String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> getById(long requestId, long userId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> create(long userId, CreateItemDto dto) {
        return post("/", userId, dto);
    }

    public ResponseEntity<Object> update(long requestId, long userId, UpdateItemDto dto) {
        return patch("/" + requestId, userId, dto);
    }

    public ResponseEntity<Object> delete(long requestId) {
        return delete("/" + requestId);
    }

    public ResponseEntity<Object> comment(long requestId, long userId, CreateCommentDto dto) {
        return post("/" + requestId + "/comment", userId, dto);
    }
}