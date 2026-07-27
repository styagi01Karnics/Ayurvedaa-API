package com.ayurveda.medicine.dto.response;

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
public class StockStatusBreakdownResponse {

    private long inStock;
    private long outOfStock;
    private long lowStock;

}
