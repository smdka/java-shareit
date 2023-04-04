package ru.practicum.shareit.booking.validate;

import ru.practicum.shareit.booking.dto.IncomingBookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateValidator implements ConstraintValidator<ValidDates, IncomingBookingDto> {
    @Override
    public boolean isValid(IncomingBookingDto value, ConstraintValidatorContext context) {
        return value.getStart().isBefore(value.getEnd()) &&
                !value.getStart().isEqual(value.getEnd());
    }
}
