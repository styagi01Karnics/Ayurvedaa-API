package com.ayurveda.medicine.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMedicineStockResponse {

    /** Total stock quantity (e.g. 442). */
    private long totalStock;

    private long tablets;
    private long syrups;
    private long powder;

    /** For the In Stock / Out of Stock / Low Stock bar. */
    private StockStatusBreakdownResponse statusBreakdown;

    /** Preview list for Dashboard Low Stock section. */
    private List<MedicineResponse> lowStockItems;

}
