package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.StartBeforeEndDateValid;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEndDateValid
public class CreateBookingDto {
    @NotNull(message = "ItemId is required")
    Long itemId;

    @FutureOrPresent(message = "Start must be in future or present")
    LocalDateTime start;

    @Future(message = "End must be in future")
    LocalDateTime end;
}