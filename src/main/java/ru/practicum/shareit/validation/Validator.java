package ru.practicum.shareit.validation;

import org.springframework.validation.BindingResult;

import javax.validation.ValidationException;

public class Validator {

    private Validator() {

    }

    public static void ifHasErrorsThrowValidationException(BindingResult br) {
        if (br.hasErrors()) {
            throw new ValidationException(br.getFieldErrors().toString());
        }
    }
}
