package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    Booking toBooking(CreateBookingDto dto);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    ShortBookingDto toShortBookingDto(Booking booking);
}
