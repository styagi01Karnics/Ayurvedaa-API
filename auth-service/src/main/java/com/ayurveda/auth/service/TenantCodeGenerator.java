package com.ayurveda.auth.service;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ayurveda.auth.constant.AuthMessages;
import com.ayurveda.common.exception.BadRequestException;

/**
 * Builds hospital tenant codes as {@code BRAND-STATE}, e.g. {@code GAN-DL}, {@code GAN-UK}.
 * On collision, allocates {@code BRAND-STATE-2}, {@code BRAND-STATE-3}, …
 */
@Component
public class TenantCodeGenerator {

    /** Safety cap so onboard never loops forever if uniqueness checks misbehave. */
    private static final int MAX_SUFFIX = 9_999;

    private static final Set<String> SKIP_WORDS = Set.of(
            "AYURVEDA", "AYURVEDIC", "HOSPITAL", "CLINIC", "THE", "AND", "OF", "PVT", "LTD", "LIMITED");

    private static final Map<String, String> STATE_CODES = new LinkedHashMap<>();

    static {
        putState("DL", "DELHI");
        putState("OD", "ODISHA", "ORISSA");
        putState("UK", "UTTARAKHAND", "UTTARANCHAL");
        putState("UP", "UTTAR PRADESH", "UTTARPRADESH");
        putState("MH", "MAHARASHTRA");
        putState("KA", "KARNATAKA");
        putState("TN", "TAMIL NADU", "TAMILNADU");
        putState("KL", "KERALA");
        putState("GJ", "GUJARAT");
        putState("RJ", "RAJASTHAN");
        putState("MP", "MADHYA PRADESH", "MADHYAPRADESH");
        putState("WB", "WEST BENGAL", "WESTBENGAL");
        putState("PB", "PUNJAB");
        putState("HR", "HARYANA");
        putState("BR", "BIHAR");
        putState("JH", "JHARKHAND");
        putState("CG", "CHHATTISGARH", "CHATTISGARH");
        putState("AS", "ASSAM");
        putState("TS", "TELANGANA");
        putState("AP", "ANDHRA PRADESH", "ANDHRAPRADESH");
        putState("GA", "GOA");
        putState("HP", "HIMACHAL PRADESH", "HIMACHALPRADESH");
        putState("JK", "JAMMU AND KASHMIR", "JAMMU & KASHMIR", "JAMMU KASHMIR");
        putState("LA", "LADAKH");
        putState("CH", "CHANDIGARH");
        putState("PY", "PUDUCHERRY", "PONDICHERRY");
    }

    private static void putState(String code, String... names) {
        STATE_CODES.put(code, code);
        for (String name : names) {
            STATE_CODES.put(normalizeKey(name), code);
        }
    }

    /**
     * @param hospitalName e.g. "Ganesha Ayurveda"
     * @param state        e.g. "Delhi", "DL", "Uttarakhand", "UK"
     * @return e.g. "GAN-DL"
     */
    public String generate(String hospitalName, String state) {
        String brand = extractBrandCode(hospitalName);
        String stateCode = resolveStateCode(state);
        return brand + "-" + stateCode;
    }

    /**
     * Returns {@code baseCode} when free; otherwise {@code baseCode-2}, {@code baseCode-3}, …
     * until {@code isTaken} is false for the candidate.
     *
     * @param baseCode BRAND-STATE from {@link #generate(String, String)}
     * @param isTaken  true when tenantCode or derived schemaName is already in use
     */
    public String allocateUnique(String baseCode, Predicate<String> isTaken) {
        if (!StringUtils.hasText(baseCode)) {
            throw new BadRequestException(AuthMessages.CANNOT_BUILD_TENANT_CODE_FROM_NAME);
        }
        String base = baseCode.trim().toUpperCase(Locale.ROOT);
        if (!isTaken.test(base)) {
            return base;
        }
        for (int n = 2; n <= MAX_SUFFIX; n++) {
            String candidate = base + "-" + n;
            if (!isTaken.test(candidate)) {
                return candidate;
            }
        }
        throw new BadRequestException(AuthMessages.TENANT_CODE_ALLOCATION_EXHAUSTED + base);
    }

    public String resolveStateCode(String state) {
        if (!StringUtils.hasText(state)) {
            throw new BadRequestException(AuthMessages.STATE_REQUIRED);
        }
        String key = normalizeKey(state);
        String code = STATE_CODES.get(key);
        if (code == null && key.length() == 2) {
            code = STATE_CODES.get(key.toUpperCase(Locale.ROOT));
        }
        if (code == null) {
            throw new BadRequestException(AuthMessages.INVALID_STATE + state.trim());
        }
        return code;
    }

    public String resolveStateDisplayName(String state) {
        String code = resolveStateCode(state);
        // Prefer original trimmed input when it looks like a name; else use code.
        String trimmed = state.trim();
        if (trimmed.length() > 2) {
            return capitalizeWords(trimmed);
        }
        return code;
    }

    private String extractBrandCode(String hospitalName) {
        if (!StringUtils.hasText(hospitalName)) {
            throw new BadRequestException(AuthMessages.HOSPITAL_NAME_REQUIRED);
        }
        String[] parts = hospitalName.trim().toUpperCase(Locale.ROOT).split("[^A-Z0-9]+");
        for (String part : parts) {
            if (part.isBlank() || SKIP_WORDS.contains(part)) {
                continue;
            }
            String letters = part.replaceAll("[^A-Z]", "");
            if (letters.length() >= 3) {
                return letters.substring(0, 3);
            }
            if (letters.length() >= 2) {
                return letters;
            }
        }
        throw new BadRequestException(AuthMessages.CANNOT_BUILD_TENANT_CODE_FROM_NAME);
    }

    private static String normalizeKey(String value) {
        return value.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", " ").trim();
    }

    private static String capitalizeWords(String value) {
        String[] words = value.toLowerCase(Locale.ROOT).split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                sb.append(word.substring(1));
            }
        }
        return sb.toString();
    }

}
