package ru.practicum.shareit.utils;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.IncorrectParameterException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class Validation {
    public static void checkPositiveId(Class<?> inputClass, Long id) {
        if (id <= 0) {
            if (inputClass.equals(User.class)) {
                throw new IncorrectParameterException("userId");
            } else if (inputClass.equals(Item.class)) {
                throw new IncorrectParameterException("itemId");
            } else if (inputClass.equals(Booking.class)) {
                throw new IncorrectParameterException("bookingId");
            } else if (inputClass.equals(ItemRequest.class)) {
                throw new IncorrectParameterException("requestId");
            }
        }
    }

    public static void checkPositivePagination(int from, int size) {
        if (from == 0 && size == 0) {
            throw new IncorrectParameterException("from, size");
        } else if (from < 0) {
            throw new IncorrectParameterException("from");
        } else if (size <= 0) {
            throw new IncorrectParameterException("size");
        }
    }
}
