package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.core.exception.FieldValidationException;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.utils.TestUtils;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingJpaRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentJpaRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestJpaRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    ItemJpaRepository repo;
    @Mock
    RequestJpaRepository requestRepo;

    @Mock
    BookingJpaRepository bookingRepo;

    @Mock
    CommentJpaRepository commentRepo;

    @Mock
    UserService userService;

    @Spy
    ItemMapper mapper = Mappers.getMapper(ItemMapper.class);

    @Spy
    CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Spy
    BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @InjectMocks
    ItemService service;

    @Test
    void search_shouldReturnEmptyListIfTextIsBlank() {
        assertThat(service.searchByText("", null)).isEmpty();
    }

    @Test
    void search_shouldReturnListOfItems() {
        List<Item> items = List.of(
                TestUtils.makeItem(1L, true, null),
                TestUtils.makeItem(2L, true, null),
                TestUtils.makeItem(3L, true, null)
        );
        List<ItemDto> listOfItemDto = items
                .stream()
                .map(mapper::toItemDto)
                .collect(Collectors.toList());

        when(repo.findAllByText(anyString(), any())).thenReturn(items);
        assertThat(service.searchByText("text", null)).isEqualTo(listOfItemDto);
    }

    @Test
    void create_shouldThrowNotFoundExceptionIfUserIsNotExists() {
        long userId = 1;
        when(userService.getById(userId)).thenThrow(NotFoundException.class);
        assertThatThrownBy(() -> service.create(userId, null)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldCreateItemWithRequest() {
        long userId = 1L;
        long requestId = 1L;
        User user = TestUtils.makeUser(1L);
        Request request = TestUtils.makeRequest(requestId, LocalDateTime.now(), user);
        CreateItemDto createItemDto = TestUtils.makeCreateItemDto(true, requestId);

        assertThatThrownBy(() -> service.create(userId, createItemDto))
                .isInstanceOf(NotFoundException.class);
        when(userService.getById(userId)).thenReturn(user);
        when(requestRepo.findById(userId)).thenReturn(Optional.of(request));
        when(repo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto itemDto = service.create(userId, createItemDto);

        assertThat(itemDto.getRequestId()).isEqualTo(requestId);
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getComments()).isNull();
        assertThat(itemDto.getOwner()).isEqualTo(user);
    }

    @Test
    void update_shouldThrowNotFoundExceptionIfUserIsNotExists() {
        long itemId = 1L;
        long userId = 1L;
        User user = TestUtils.makeUser(userId);
        User user2 = TestUtils.makeUser(2L);
        Item item = TestUtils.makeItem(itemId, true, user);
        UpdateItemDto updateItemDto = new UpdateItemDto("name", "description", true);

        when(userService.getById(userId)).thenReturn(user);
        when(userService.getById(user2.getId())).thenReturn(user2);
        when(repo.findById(itemId)).thenReturn(Optional.of(item));
        assertThatThrownBy(() -> service.update(itemId, user2.getId(), updateItemDto)).isInstanceOf(NotFoundException.class);

        when(userService.getById(userId)).thenThrow(NotFoundException.class);
        assertThatThrownBy(() -> service.update(itemId, userId, null)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldThrowNotFoundExceptionIfRequestIsNotExists() {
        long itemId = 1L;
        long userId = 1L;

        when(repo.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(itemId, userId, null)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldUpdateItemName() {
        long itemId = 1L;
        long userId = 1L;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        UpdateItemDto updateItemDto = new UpdateItemDto("new name", null, null);

        when(userService.getById(userId)).thenReturn(user);
        when(repo.findById(itemId)).thenReturn(Optional.of(item));
        when(repo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto itemDto = service.update(itemId, userId, updateItemDto);

        assertThat(itemDto.getName()).isEqualTo("new name");
    }

    @Test
    void update_shouldUpdateItemDescription() {
        long itemId = 1L;
        long userId = 1L;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        UpdateItemDto updateItemDto = new UpdateItemDto(null, "new description", null);

        when(userService.getById(userId)).thenReturn(user);
        when(repo.findById(itemId)).thenReturn(Optional.of(item));
        when(repo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto itemDto = service.update(itemId, userId, updateItemDto);

        assertThat(itemDto.getDescription()).isEqualTo("new description");
    }

    @Test
    void update_shouldUpdateItemAvailable() {
        long itemId = 1L;
        long userId = 1L;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        UpdateItemDto updateItemDto = new UpdateItemDto(null, null, false);

        when(userService.getById(userId)).thenReturn(user);
        when(repo.findById(itemId)).thenReturn(Optional.of(item));
        when(repo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        ItemDto itemDto = service.update(itemId, userId, updateItemDto);

        assertThat(itemDto.getAvailable()).isFalse();
    }

    @Test
    void comment_shouldCommentRequest() {
        long itemId = 1L;
        long userId = 1L;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        CreateCommentDto createCommentDto = new CreateCommentDto("new comment");

        when(userService.getById(userId)).thenReturn(user);
        when(repo.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepo.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                anyLong(), any(), any()
        )).thenReturn(Collections.emptyList());
        when(commentRepo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        assertThatThrownBy(() -> service.comment(itemId, userId, createCommentDto))
                .isInstanceOf(FieldValidationException.class);

        when(bookingRepo.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()
        )).thenReturn(List.of(
                new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.APPROVED)));

        CommentDto commentDto = service.comment(itemId, userId, createCommentDto);
        assertThat(commentDto.getAuthorName()).isEqualTo(user.getName());
        assertThat(commentDto.getId()).isEqualTo(0L);
        assertThat(commentDto.getText()).isEqualTo("new comment");
    }

    @Test
    void delete_shouldDeleteItemAndReturnDeletedItem_should() {
        long itemId = 1L;
        long userId = 1L;
        User user = TestUtils.makeUser(1L);
        Item item = TestUtils.makeItem(1L, true, user);
        repo.delete(item);

        verify(repo, times(1)).delete(item);

        when(repo.findById(itemId)).thenReturn(Optional.of(item));
        when(repo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        ItemDto itemDto = service.delete(itemId);

        assertThat(itemDto.getId().equals(itemId));
    }

    @Test
    void getById_should() {
        long itemId = 1L;
        long userId = 1L;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);

        when(repo.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepo.findAllByItemIdOrderByStartAsc(anyLong())).thenReturn(Collections.emptyList());
        when(commentRepo.findAllByItemId(anyLong())).thenReturn(Collections.emptyList());

        assertThat(service.getById(itemId, userId)).isEqualTo(mapper.toItemDto(item));
    }

    @Test
    void getByUserId_should() {
        User user = TestUtils.makeUser(1L);
        List<Item> items = List.of(TestUtils.makeItem(1L, true, user),
                TestUtils.makeItem(2L, true, user));

        when(repo.findAllByOwnerIdOrderById(1L, null)).thenReturn(items);

        assertThat(service.getByUserId(1L, null)).hasAtLeastOneElementOfType(ItemDto.class);

    }
}
