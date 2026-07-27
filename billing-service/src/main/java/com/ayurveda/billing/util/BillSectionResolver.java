package com.ayurveda.billing.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ayurveda.billing.dto.request.CreateInvoiceRequest;
import com.ayurveda.billing.enums.BillSection;
import com.ayurveda.common.exception.BadRequestException;

public final class BillSectionResolver {

    private BillSectionResolver() {
    }

    public static List<BillSection> resolveFromRequest(CreateInvoiceRequest request) {
        List<BillSection> sections = new ArrayList<>();

        if (moneyPositive(request.getServiceFees()) || moneyPositive(request.getPackageCharges())) {
            sections.add(BillSection.SERVICE);
        }
        if (!CollectionUtils.isEmpty(request.getMedicines())) {
            sections.add(BillSection.MEDICINE);
        }
        if (!CollectionUtils.isEmpty(request.getTherapies())) {
            sections.add(BillSection.THERAPY);
        }

        if (sections.isEmpty()) {
            throw new BadRequestException(
                    "Invoice must include at least one section: Service Type, Medicine, or Therapy.");
        }

        return sections;
    }

    public static String toStorage(List<BillSection> sections) {
        return sections.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }

    public static List<BillSection> fromStorage(String value) {
        if (!StringUtils.hasText(value)) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(BillSection::valueOf)
                .toList();
    }

    private static boolean moneyPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

}
