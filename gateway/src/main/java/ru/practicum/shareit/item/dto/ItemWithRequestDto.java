package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemWithRequestDto extends ItemDto {
    private Long requestId;

    public ItemWithRequestDto(Item item, Long requestId) {
        super(item.getId(), item.getName(), item.getDescription(), item.isAvailable());
        this.requestId = requestId;
    }
}
