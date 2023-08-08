package ru.yandex.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.shareit.booking.dto.BookingState;
import ru.yandex.practicum.shareit.booking.dto.CreateBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(path = "/bookings")
public class BookingController {
    static final String USER_ID_HEADER = "X-Sharer-User-Id";
    BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAllByBooker(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size
    ) {
        BookingState state = checkBookingState(stateParam);
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(
            @RequestHeader(USER_ID_HEADER) long ownerId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size
    ) {
        BookingState state = checkBookingState(stateParam);
        log.info("Get booking with state {}, ownerId={}, from={}, size={}", stateParam, ownerId, from, size);
        return bookingClient.getAllByOwner(ownerId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) long userId, @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getById(userId, bookingId);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestBody @Valid CreateBookingDto requestDto
    ) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.create(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(
            @PathVariable long bookingId,
            @RequestHeader(USER_ID_HEADER) long ownerId,
            @RequestParam boolean approved
    ) {
        log.info("Update booking {}, userId={}", approved, ownerId);
        return bookingClient.update(bookingId, ownerId, approved);
    }

    private BookingState checkBookingState(String stateParam) {
        return BookingState.from(stateParam).orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
    }
}