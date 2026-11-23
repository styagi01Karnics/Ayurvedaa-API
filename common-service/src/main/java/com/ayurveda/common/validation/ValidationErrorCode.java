package com.ayurveda.common.validation;

public enum ValidationErrorCode {

    NAME_LENGTH_INVALID("Name must be between 2 and 150 characters"),
    NAME_INVALID_CHARACTERS("Name can contain only letters, spaces, hyphen (-) and parentheses ()"),
    NAME_INVALID_CHARACTERS_WITH_PERIOD(
            "Name can contain only letters, spaces, hyphen (-), period (.) and parentheses ()");

    private final String message;

    ValidationErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static ValidationErrorCode fromName(String name) {
        for (ValidationErrorCode code : values()) {
            if (code.name().equals(name)) {
                return code;
            }
        }
        return null;
    }

}
