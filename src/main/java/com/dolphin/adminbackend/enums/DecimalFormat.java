package com.dolphin.adminbackend.enums;

public enum DecimalFormat {
    WHOLE("0"),
    ONE("0.0"),
    TWO("0.00");

    private final String format;

    DecimalFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}
