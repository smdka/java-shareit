package ru.practicum.shareit.booking.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDates {
    String message() default "Неверные даты начала и конца бронирования";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
