package com.airtribe.ridewise.util;

public enum IdPrefix {
    RIDER("RDR"),
    DRIVER("DRI"),
    RIDE("RID");

    private final String code;

    IdPrefix(String code) { this.code = code; }
    public String getCode() { return code; }
}
