package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemOutputDto extends ItemDto {

    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentDto> comments;

    public ItemOutputDto(Item item, BookingShortDto lastBooking, BookingShortDto nextBooking) {
        super(item.getId(), item.getName(), item.getDescription(), item.isAvailable());
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
    }
}
