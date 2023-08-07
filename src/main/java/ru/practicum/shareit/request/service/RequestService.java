package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestJpaRepository;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.item.ItemJpaRepository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestMapper mapper;
    private final RequestJpaRepository repo;
    private final ItemJpaRepository itemRepo;
    private final ItemMapper itemMapper;
    private final UserService userService;

    public RequestDto createRequest(CreateRequestDto dto, long userId) {
        User user = userService.getById(userId);

        Request request = mapper.toRequest(dto);
        request.setUser(user);
        request.setCreated(LocalDateTime.now());
        request = repo.save(request);

        return mapper.toRequestDto(request);
    }

    public List<RequestDto> getOwnRequests(long userId) {
        userService.getById(userId);

        List<RequestDto> requestsList = repo.findAllByUserIdOrderByCreatedDesc(userId)
                .stream()
                .map(mapper::toRequestDto)
                .collect(Collectors.toList());
        if (!requestsList.isEmpty()) {
            requestsList = setItemCollectionForRequestsList(requestsList);
        }
        return requestsList;

    }

    public List<RequestDto> getOtherRequests(long userId, Pageable pageable) {
        userService.getById(userId);

        List<RequestDto> requestsList = repo.findAllByUserIdIsNotOrderByCreatedDesc(userId, pageable)
                .stream()
                .map(mapper::toRequestDto)
                .collect(Collectors.toList());
        if (!requestsList.isEmpty()) {
            requestsList = setItemCollectionForRequestsList(requestsList);
        }
        return requestsList;
    }

    public RequestDto getById(long requestId, long userId) {
        userService.getById(userId);

        Request request = repo
                .findById(requestId)
                .orElseThrow(() -> new NotFoundException("request", requestId));

        return makeOneRequestDtoWithItemCollection(request);
    }

    private RequestDto makeOneRequestDtoWithItemCollection(Request request) {
        RequestDto requestDto = mapper.toRequestDto(request);

        List<ItemDto> items = itemRepo
                .findAllByRequestId(request.getId())
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        requestDto.setItems(items);

        return requestDto;
    }

    private List<RequestDto> setItemCollectionForRequestsList(List<RequestDto> requestDtoList) {
        Map<Long, List<ItemDto>> items = itemRepo
                .findAllWithRequestId()
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.groupingBy(
                        ItemDto::getRequestId));
        return requestDtoList.stream()
                .peek(requestDto -> requestDto.setItems(
                        items.getOrDefault(requestDto.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }
}
