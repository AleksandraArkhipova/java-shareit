package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    Booking ToBooking(BookingDto dto);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    ShortBookingDto toShortBookingDto(Booking booking);
}
