package com.ayurveda.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Shared page/size clamping for list APIs (default size 20, max 100).
 */
public final class PageRequests {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    private PageRequests() {
    }

    public static PageRequest of(int page, int size) {
        return of(page, size, Sort.unsorted());
    }

    public static PageRequest of(int page, int size, Sort sort) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        return sort == null || sort.isUnsorted()
                ? PageRequest.of(safePage, safeSize)
                : PageRequest.of(safePage, safeSize, sort);
    }

    /** In-memory slice after filtering a full list. */
    public static <T> java.util.List<T> slice(java.util.List<T> all, int page, int size) {
        if (all == null || all.isEmpty()) {
            return java.util.List.of();
        }
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        int from = safePage * safeSize;
        if (from >= all.size()) {
            return java.util.List.of();
        }
        int to = Math.min(from + safeSize, all.size());
        return all.subList(from, to);
    }
}
