package com.wairixx.AdultsEducation.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import java.util.List;

public final class SpecificationUtils {

    private SpecificationUtils() {}

    public static <T> void addIfNotNull(List<Specification<T>> list, Specification<T> spec) {
        if (spec != null) list.add(spec);
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> rangeSpec(
            String field, Y min, Y max) {
        if (min == null && max == null) return null;
        return (root, q, cb) -> {
            if (min != null && max != null) return cb.between(root.get(field), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get(field), min);
            return cb.lessThanOrEqualTo(root.get(field), max);
        };
    }

    public static <T> Specification<T> likeIgnoreCase(String field, String value) {
        if (value == null || value.isBlank()) return null;
        return (root, q, cb) -> cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
    }

    public static <T> Specification<T> alwaysTrue() {
        return (root, q, cb) -> cb.conjunction();
    }
}