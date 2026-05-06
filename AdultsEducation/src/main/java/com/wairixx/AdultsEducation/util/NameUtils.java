package com.wairixx.AdultsEducation.util;

import java.util.stream.Stream;

public final class NameUtils {
    private NameUtils() {}

    /** "Прізвище Ім'я" (у такому порядку, пропускає null/blank). */
    public static String fullName(String lastName, String firstName) {
        return Stream.of(lastName, firstName)
                .filter(s -> s != null && !s.isBlank())
                .reduce((a, b) -> a + " " + b)
                .orElse("");
    }
}