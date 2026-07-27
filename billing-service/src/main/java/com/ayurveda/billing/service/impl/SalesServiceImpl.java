package com.ayurveda.billing.service.impl;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ayurveda.billing.client.AppointmentServiceClient;
import com.ayurveda.billing.constant.BillingMessages;
import com.ayurveda.billing.dto.client.AppointmentTherapyClientResponse;
import com.ayurveda.billing.dto.response.DashboardBillingSummaryResponse;
import com.ayurveda.billing.dto.response.MonthlyRevenueResponse;
import com.ayurveda.billing.dto.response.SalesInvoiceResponse;
import com.ayurveda.billing.dto.response.SalesPageResponse;
import com.ayurveda.billing.entity.Invoice;
import com.ayurveda.billing.enums.BillingPeriod;
import com.ayurveda.billing.repository.InvoiceRepository;
import com.ayurveda.billing.service.SalesService;
import com.ayurveda.billing.util.InvoiceCalculationUtil;
import com.ayurveda.common.ApiResponse;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesServiceImpl implements SalesService {

    private final InvoiceRepository invoiceRepository;
    private final AppointmentServiceClient appointmentServiceClient;

    @Override
    public ApiResponse<SalesPageResponse> getSales(String serviceType, LocalDate dateCreated) {
        String serviceFilter = StringUtils.hasText(serviceType) ? serviceType.trim() : null;

        List<Invoice> invoices = invoiceRepository.searchSales(serviceFilter, dateCreated);
        Map<UUID, String> categoryCache = new HashMap<>();

        List<SalesInvoiceResponse> sales = invoices.stream()
                .map(invoice -> toSalesRow(invoice, categoryCache))
                .toList();

        YearMonth currentMonth = YearMonth.now();
        LocalDate from = currentMonth.atDay(1);
        LocalDate to = currentMonth.atEndOfMonth();
        BigDecimal revenue = InvoiceCalculationUtil.money(
                invoiceRepository.sumTotalAmountBetween(from, to));

        SalesPageResponse response = SalesPageResponse.builder()
                .revenueThisMonth(revenue)
                .revenueFrom(from)
                .revenueTo(to)
                .sales(sales)
                .build();

        return ApiResponse.success(BillingMessages.SALES_FETCHED_SUCCESSFULLY, response);
    }

    @Override
    public ApiResponse<MonthlyRevenueResponse> getMonthlyRevenue(Integer year, Integer month) {
        YearMonth yearMonth = resolveYearMonth(year, month);
        LocalDate from = yearMonth.atDay(1);
        LocalDate to = yearMonth.atEndOfMonth();

        BigDecimal totalRevenue = InvoiceCalculationUtil.money(
                invoiceRepository.sumTotalAmountBetween(from, to));
        long invoiceCount = invoiceRepository.countBetween(from, to);

        MonthlyRevenueResponse response = MonthlyRevenueResponse.builder()
                .year(yearMonth.getYear())
                .month(yearMonth.getMonthValue())
                .fromDate(from)
                .toDate(to)
                .totalRevenue(totalRevenue)
                .invoiceCount(invoiceCount)
                .build();

        return ApiResponse.success(BillingMessages.MONTHLY_REVENUE_FETCHED_SUCCESSFULLY, response);
    }

    @Override
    public ApiResponse<DashboardBillingSummaryResponse> getDashboardBillingSummary(BillingPeriod period) {
        BillingPeriod selectedPeriod = period != null ? period : BillingPeriod.MONTHLY;
        LocalDate[] range = resolvePeriodRange(selectedPeriod);
        LocalDate from = range[0];
        LocalDate to = range[1];

        DashboardBillingSummaryResponse response = DashboardBillingSummaryResponse.builder()
                .period(selectedPeriod)
                .fromDate(from)
                .toDate(to)
                .totalRevenue(InvoiceCalculationUtil.money(invoiceRepository.sumTotalAmountBetween(from, to)))
                .totalBillsGenerated(invoiceRepository.countBetween(from, to))
                .pendingPayments(InvoiceCalculationUtil.money(invoiceRepository.sumLeftAmountBetween(from, to)))
                .collectedPayments(InvoiceCalculationUtil.money(invoiceRepository.sumPaidAmountBetween(from, to)))
                .build();

        return ApiResponse.success(BillingMessages.DASHBOARD_BILLING_SUMMARY_FETCHED, response);
    }

    private LocalDate[] resolvePeriodRange(BillingPeriod period) {
        LocalDate today = LocalDate.now();
        return switch (period) {
            case WEEKLY -> {
                LocalDate from = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate to = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                yield new LocalDate[] { from, to };
            }
            case YEARLY -> {
                Year year = Year.now();
                yield new LocalDate[] { year.atDay(1), year.atMonth(12).atEndOfMonth() };
            }
            case MONTHLY -> {
                YearMonth month = YearMonth.now();
                yield new LocalDate[] { month.atDay(1), month.atEndOfMonth() };
            }
        };
    }

    private SalesInvoiceResponse toSalesRow(Invoice invoice, Map<UUID, String> categoryCache) {
        return SalesInvoiceResponse.builder()
                .invoiceId(invoice.getInvoiceNumber())
                .invoiceDate(invoice.getInvoiceDate())
                .treatmentCategory(resolveTreatmentCategory(invoice.getPatientId(), categoryCache))
                .serviceType(resolveServiceType(invoice))
                .totalAmount(invoice.getTotalAmount())
                .build();
    }

    private String resolveServiceType(Invoice invoice) {
        if (StringUtils.hasText(invoice.getPackageType())) {
            return invoice.getPackageType();
        }
        if (invoice.getVisitType() != null) {
            return formatEnum(invoice.getVisitType().name());
        }
        if (StringUtils.hasText(invoice.getBillSections())) {
            return invoice.getBillSections().replace(",", " / ");
        }
        return null;
    }

    private String resolveTreatmentCategory(UUID patientId, Map<UUID, String> cache) {
        if (patientId == null) {
            return null;
        }
        if (cache.containsKey(patientId)) {
            return cache.get(patientId);
        }

        String category = null;
        try {
            ApiResponse<List<AppointmentTherapyClientResponse>> response =
                    appointmentServiceClient.getTherapiesByPatientId(patientId);

            if (response != null && response.getData() != null) {
                category = response.getData().stream()
                        .filter(item -> item.getTreatmentCategory() != null)
                        .map(item -> item.getTreatmentCategory().getCategoryName())
                        .filter(StringUtils::hasText)
                        .findFirst()
                        .orElse(null);
            }
        } catch (FeignException ex) {
            log.debug("Treatment category not found for patient {}: {}", patientId, ex.getMessage());
        } catch (Exception ex) {
            log.debug("Ignoring treatment category lookup for patient {}", patientId, ex);
        }

        cache.put(patientId, category);
        return category;
    }

    private YearMonth resolveYearMonth(Integer year, Integer month) {
        YearMonth now = YearMonth.now();
        int y = year != null ? year : now.getYear();
        int m = month != null ? month : now.getMonthValue();
        return YearMonth.of(y, m);
    }

    private String formatEnum(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        String lower = value.toLowerCase().replace('_', ' ');
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

}
