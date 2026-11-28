package com.ayurveda.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Name rule: 3-80 characters after trim. By default only letters, spaces and hyphen are allowed (person names).
 * Set {@code allowPeriod} for doctor names and {@code allowSpecialCharacters} for master names, which allow
 * letters, digits and symbols. Blank or null values are left to {@code @NotBlank}.
 */
@Documented
@Constraint(validatedBy = ValidNameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidName {

    String message() default "NAME_INVALID_CHARACTERS";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean allowPeriod() default false;

    boolean allowSpecialCharacters() default false;

}
