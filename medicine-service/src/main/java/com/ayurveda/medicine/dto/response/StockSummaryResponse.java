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
public class StockSummaryResponse {

    private long totalStock;

    private List<CategoryStockCountResponse> byCategory;

}
