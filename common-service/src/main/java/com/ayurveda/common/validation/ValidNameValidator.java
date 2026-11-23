package com.ayurveda.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ValidNameValidator implements ConstraintValidator<ValidName, String> {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 150;
    private static final Pattern NAME_CHARACTERS = Pattern.compile("^[\\p{L} \\-()]+$");
    private static final Pattern NAME_CHARACTERS_WITH_PERIOD = Pattern.compile("^[\\p{L} \\-().]+$");

    private boolean allowPeriod;

    @Override
    public void initialize(ValidName annotation) {
        this.allowPeriod = annotation.allowPeriod();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        String name = value.trim();
        if (name.length() < MIN_LENGTH || name.length() > MAX_LENGTH) {
            return reject(context, ValidationErrorCode.NAME_LENGTH_INVALID);
        }
        Pattern allowed = allowPeriod ? NAME_CHARACTERS_WITH_PERIOD : NAME_CHARACTERS;
        if (!allowed.matcher(name).matches()) {
            return reject(context, allowPeriod
                    ? ValidationErrorCode.NAME_INVALID_CHARACTERS_WITH_PERIOD
                    : ValidationErrorCode.NAME_INVALID_CHARACTERS);
        }
        return true;
    }

    private boolean reject(ConstraintValidatorContext context, ValidationErrorCode code) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(code.name()).addConstraintViolation();
        return false;
    }

}
