package com.ayurveda.common.validation;

import org.springframework.util.StringUtils;

import com.ayurveda.common.constant.AppConstants;
import com.ayurveda.common.enums.IdProofType;
import com.ayurveda.common.exception.BadRequestException;

public final class IdProofValidator {

    private IdProofValidator() {
    }

    public static void validate(IdProofType proofType, String proofNumber) {
        if (proofType == null || !StringUtils.hasText(proofNumber)) {
            return;
        }

        String normalized = proofNumber.trim().toUpperCase();

        switch (proofType) {
            case AADHAAR -> requireMatch(normalized, ValidationPatterns.AADHAAR, AppConstants.INVALID_AADHAAR);
            case PAN -> requireMatch(normalized, ValidationPatterns.PAN, AppConstants.INVALID_PAN);
            case PASSPORT -> requireMatch(normalized, ValidationPatterns.PASSPORT, AppConstants.INVALID_PASSPORT);
            case DRIVING_LICENSE -> requireMatch(
                    normalized, ValidationPatterns.DRIVING_LICENSE, AppConstants.INVALID_DRIVING_LICENSE);
            case VOTER_ID -> requireMatch(normalized, ValidationPatterns.VOTER_ID, AppConstants.INVALID_VOTER_ID);
            default -> throw new BadRequestException(AppConstants.INVALID_ID_PROOF);
        }
    }

    public static void validate(String proofType, String proofNumber) {
        if (!StringUtils.hasText(proofType) || !StringUtils.hasText(proofNumber)) {
            return;
        }
        try {
            validate(IdProofType.valueOf(proofType.trim().toUpperCase()), proofNumber);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(AppConstants.INVALID_ID_PROOF);
        }
    }

    private static void requireMatch(String value, String pattern, String errorMessage) {
        if (!value.matches(pattern)) {
            throw new BadRequestException(errorMessage);
        }
    }

}
