package ru.yandex.practicum.filmorate.entity.user;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserLoginValidator implements ConstraintValidator<UserLogin, String> {

    @Override
    public boolean isValid(String userLogin, ConstraintValidatorContext context) {
        return !userLogin.isBlank() && !userLogin.contains(" ");
    }
}
