package com.ayurveda.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Person/master name rule: 2-150 characters after trim, letters, spaces, hyphen and parentheses only.
 * Blank or null values are left to {@code @NotBlank}. Set {@code allowPeriod} for doctor names.
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

}
