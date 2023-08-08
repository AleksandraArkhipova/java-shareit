package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.utils.TestUtils;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.core.exception.FieldValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemJpaRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.BookingState.*;
import static ru.practicum.shareit.booking.BookingState.FUTURE;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    BookingJpaRepository repo;

    @Spy
    BookingMapper mapper = Mappers.getMapper(BookingMapper.class);

    @Mock
    UserService userService;

    @Mock
    ItemService itemService;

    @Mock
    ItemJpaRepository itemRepo;

    @InjectMocks
    BookingService service;

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdOrderByStartDesc() {
        service.getAllByBooker(1L, ALL, null);
        verify(repo).findAllByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        service.getAllByBooker(1L, CURRENT, null);
        verify(repo)
                .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any());
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndEndBeforeOrderByStartDesc() {
        service.getAllByBooker(1L, PAST, null);
        verify(repo)
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndStartAfterOrderByStartDesc() {
        service.getAllByBooker(1L, FUTURE, null);
        verify(repo)
                .findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndStatusOrderByStartDescWithStatusWaiting() {
        service.getAllByBooker(1L, WAITING, null);
        verify(repo)
                .findAllByBookerIdAndStatusOrderByStartDesc(1, BookingStatus.WAITING, null);
    }

    @Test
    void getAllByBooker_shouldCallFindAllByBookerIdAndStatusOrderByStartDescWithStatusRejected() {
        service.getAllByBooker(1L, REJECTED, null);
        verify(repo)
                .findAllByBookerIdAndStatusOrderByStartDesc(1L, BookingStatus.REJECTED, null);
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdOrderByStartDesc() {
        service.getAllByOwner(1L, ALL, null);
        verify(repo).findAllByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        service.getAllByOwner(1L, CURRENT, null);
        verify(repo)
                .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any());
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        service.getAllByOwner(1L, PAST, null);
        verify(repo)
                .findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndStartAfterOrderByStartDesc() {
        service.getAllByOwner(1L, FUTURE, null);
        verify(repo)
                .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(), any());
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndStatusOrderByStartDescWithStatusWaiting() {
        service.getAllByOwner(1L, WAITING, null);
        verify(repo)
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(1, BookingStatus.WAITING, null);
    }

    @Test
    void getAllByOwner_shouldCallFindAllByItemOwnerIdAndStatusOrderByStartDescWithStatusRejected() {
        service.getAllByOwner(1L, REJECTED, null);
        verify(repo)
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(1, BookingStatus.REJECTED, null);
    }

    @Test
    void create_shouldThrowNotFoundIfUserIsNotExists() {
        long userId = 1L;
        long itemId = 1L;
        CreateBookingDto dto = new CreateBookingDto(itemId, LocalDateTime.now(), LocalDateTime.now());

        when(userService.getById(userId)).thenThrow(new NotFoundException("user", userId));

        assertThatThrownBy(() -> service.create(userId, dto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldThrowNotFoundIfItemIsNotExists() {
        long userId = 1L;
        long itemId = 1L;
        CreateBookingDto dto = new CreateBookingDto(itemId, LocalDateTime.now(), LocalDateTime.now());

        when(itemRepo.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(userId, dto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldThrowFieldValidationExceptionIfItemIsUnavailable() {
        long userId = 1L;
        long itemId = 1L;
        CreateBookingDto dto = new CreateBookingDto(itemId, LocalDateTime.now(), LocalDateTime.now());
        Item item = TestUtils.makeItem(itemId, false, null);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> service.create(userId, dto)).isInstanceOf(FieldValidationException.class);
    }

    @Test
    void create_shouldThrowNotFoundIfUserIsNotOwner() {
        long userId = 1L;
        long itemId = 1L;
        CreateBookingDto dto = new CreateBookingDto(itemId, LocalDateTime.now(), LocalDateTime.now());
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> service.create(userId, dto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldThrowFieldValidationExceptionIfDateIsIncorrect() {
        long userId = 1L;
        long itemId = 1L;
        CreateBookingDto dto = new CreateBookingDto(itemId, LocalDateTime.now(), LocalDateTime.now().minusDays(1));
        User user = TestUtils.makeUser(2L);
        Item item = TestUtils.makeItem(itemId, true, user);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> service.create(userId, dto)).isInstanceOf(FieldValidationException.class);
    }

    @Test
    void create_shouldCreateBooking() {
        long userId = 1L;
        long itemId = 1L;

        CreateBookingDto dto = new CreateBookingDto(itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        CreateBookingDto dto2 = new CreateBookingDto(itemId, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4));
        CreateBookingDto dto3 = new CreateBookingDto(itemId, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4));

        User user = TestUtils.makeUser(2L);
        Item item = TestUtils.makeItem(itemId, true, user);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));
        when(userService.getById(userId)).thenReturn(user);
        when(repo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        Booking booking = service.create(userId, dto);
        verify(repo, times(1)).save(booking);

        Booking booking2 = service.create(userId, dto2);
        verify(repo, times(1)).save(booking2);

        List<Booking> bookings = List.of(booking, booking2);
        when(itemService.getAllBookings(itemId)).thenReturn(bookings);
        assertThatThrownBy(() -> service.create(userId, dto3)).isInstanceOf(FieldValidationException.class);
        CreateBookingDto dto4 = new CreateBookingDto(itemId, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(6));
        Booking booking4 = service.create(userId, dto4);

        assertThat(booking4).isInstanceOf(Booking.class);
        assertThat(booking4).hasFieldOrProperty("id");

    }

    @Test
    void update_shouldThrowNotFoundIfBookingIsNotExists() {
        long bookingId = 1L;
        long userId = 1L;

        when(repo.findById(bookingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(bookingId, userId, true)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldThrowNotFoundIfUserIsNotOwner() {
        long bookingId = 1L;
        long userId = 1L;
        long itemId = 1L;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.WAITING);

        when(repo.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.update(bookingId, 2L, true)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldThrowFieldValidationExceptionIfBookingIsAlreadyApproved() {
        long bookingId = 1L;
        long userId = 1L;
        long itemId = 1L;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.APPROVED);

        when(repo.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.update(bookingId, userId, true)).isInstanceOf(FieldValidationException.class);
    }

    @Test
    void update_shouldUpdateBookingToApproved() {
        long bookingId = 1L;
        long userId = 1L;
        long itemId = 1L;
        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.WAITING);

        when(repo.findById(bookingId)).thenReturn(Optional.of(booking));
        when(repo.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        booking = service.update(bookingId, userId, true);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void getById_shouldThrowNotFoundExceptionIfBookingIsNotExists() {
        long bookingId = 1L;
        long userId = 1L;

        when(repo.findById(bookingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(bookingId, userId)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getById_shouldThrowNotFoundExceptionIfUserIsNotOwner() {
        long bookingId = 1L;
        long userId = 1L;
        long itemId = 1L;

        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.WAITING);

        when(repo.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.getById(bookingId, 2L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getById_shouldReturnBooking() {
        long bookingId = 1L;
        long userId = 1L;
        long itemId = 1L;

        User user = TestUtils.makeUser(userId);
        Item item = TestUtils.makeItem(itemId, true, user);
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.WAITING);

        when(repo.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThat(service.getById(bookingId, userId)).isEqualTo(booking);
    }
}