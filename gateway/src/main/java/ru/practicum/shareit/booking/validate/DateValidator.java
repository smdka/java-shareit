package ru.practicum.shareit.booking.validate;

import ru.practicum.shareit.booking.dto.IncomingBookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class DateValidator implements ConstraintValidator<ValidDates, IncomingBookingDto> {
    @Override
    public void initialize(ValidDates constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(IncomingBookingDto value, ConstraintValidatorContext context) {
        LocalDateTime start = value.getStart();
        LocalDateTime end = value.getEnd();
        if (start != null && end != null) {
            return start.isBefore(end) && !start.isEqual(end);
        }
        return false;
    }
}
