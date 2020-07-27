package org.tinywind.server.util.excel;

public enum CellStyleFormat {

    CURRENCY("#,##0"), PERCENTAGE("0.00");

    private String pattern;

    CellStyleFormat(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

}
