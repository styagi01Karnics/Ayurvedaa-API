package com.ayurveda.common.dto;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;

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
public class PagedResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public static <T> PagedResponse<T> of(Page<T> page) {
        return PagedResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    public static <T> PagedResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int safeSize = size <= 0 ? 20 : size;
        int totalPages = safeSize == 0 ? 0 : (int) Math.ceil((double) totalElements / (double) safeSize);
        return PagedResponse.<T>builder()
                .content(content != null ? content : Collections.emptyList())
                .page(Math.max(page, 0))
                .size(safeSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }
}
