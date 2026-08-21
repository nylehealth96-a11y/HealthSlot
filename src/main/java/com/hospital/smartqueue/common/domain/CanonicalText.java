package com.hospital.smartqueue.common.domain;

import java.util.Locale;

public final class CanonicalText {
    private CanonicalText() {
    }

    public static String displayValue(String value) {
        return value == null ? null : value.strip();
    }

    public static String normalize(String value) {
        String displayValue = displayValue(value);
        return displayValue == null ? null : displayValue.toLowerCase(Locale.ROOT);
    }
}
