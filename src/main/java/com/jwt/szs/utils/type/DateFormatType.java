package com.jwt.szs.utils.type;

public enum DateFormatType {

    HYPHEN("-"), COMMA(".");

    String separator;
    DateFormatType(String separator){
        this.separator = separator;
    }

    public String getSeparator() {
        return separator;
    }
}
