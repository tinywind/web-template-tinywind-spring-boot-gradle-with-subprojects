package org.tinywind.server.service.excel;

import org.tinywind.server.util.excel.AbstractSinglePageExcel;
import org.apache.poi.ss.usermodel.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public abstract class AbstractExcel extends AbstractSinglePageExcel {

    protected Font boldFont;
    protected CellStyle emptyStyle;
    protected CellStyle defaultStyle;
    protected CellStyle mergedCellStyle;
    protected CellStyle sheetHeadStyle;

    public static String cellValue(Row row, int index) {
        final Cell cell = row.getCell(index);

        if (cell == null || cell.getCellType() == null)
            return "";

        switch (cell.getCellType()) {
            case NUMERIC:
                return String.format("%.0f", cell.getNumericCellValue());
            case STRING:
                return cell.getStringCellValue();
            case BLANK:
                return "";
            case BOOLEAN:
                return cell.getBooleanCellValue() ? "true" : "false";
            case ERROR:
                return cell.getErrorCellValue() + "";
        }

        return cell.getCellFormula();
    }

    public AbstractExcel() {
        super();

        sheet.setDefaultColumnWidth(20);

        boldFont = workbook.createFont();
        boldFont.setBold(true);

        emptyStyle = workbook.createCellStyle();

        defaultStyle = workbook.createCellStyle();
        defaultStyle.setBorderBottom(BorderStyle.THIN);
        defaultStyle.setBorderTop(BorderStyle.THIN);
        defaultStyle.setBorderLeft(BorderStyle.THIN);
        defaultStyle.setBorderRight(BorderStyle.THIN);
        defaultStyle.setAlignment(HorizontalAlignment.CENTER);
        defaultStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        mergedCellStyle = workbook.createCellStyle();
        mergedCellStyle.cloneStyleFrom(defaultStyle);
        mergedCellStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        mergedCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        mergedCellStyle.setFont(boldFont);

        sheetHeadStyle = workbook.createCellStyle();
        sheetHeadStyle.cloneStyleFrom(defaultStyle);
        sheetHeadStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        sheetHeadStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    public static String paddingNumber(Number number, int count) {
        if (number == null) return "";
        return String.format("%0" + count + "d", number);
    }

    public static String bytesToHex(byte[] bytes, int read, int linePrint, int lineBreakInterval) {
        final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        final java.util.List<Character> result = new ArrayList<>();

        for (int i = 0, j = 0, k = 0; i < bytes.length && i < read; i++) {
            int v = bytes[i] & 0xFF;
            result.add(HEX_ARRAY[v >>> 4]);
            result.add(HEX_ARRAY[v & 0x0F]);
            result.add(' ');

            if (++k == lineBreakInterval) {
                result.add(' ');
                result.add(' ');
                k = 0;
            }

            if (++j == linePrint) {
                result.add('\n');
                j = 0;
            }
        }
        return result.stream().map(Object::toString).reduce("", (a, b) -> a + b);
    }

    public static String niceFormat(Object o) {
        if (o == null) return "";
        if (o instanceof Double) {
            if ((double) o == ((Double) o).longValue()) return String.format("%d", ((Double) o).longValue());
            return String.format("%s", o);
        }
        if (o instanceof Float) {
            if ((float) o == ((Float) o).longValue()) return String.format("%d", ((Float) o).longValue());
            return String.format("%s", o);
        }
        if (o.getClass().isArray()) {
            final byte[] bytes = (byte[]) o;
            return bytesToHex(bytes, bytes.length, bytes.length + 1, bytes.length + 1);
        }
        if (o instanceof java.sql.Date)
            return new SimpleDateFormat("yyyy-MM-dd").format((java.util.Date) o);
        if (o instanceof java.sql.Time)
            return new SimpleDateFormat("HH:mm:ss").format((java.util.Date) o);
        if (o instanceof java.util.Date)
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((java.util.Date) o);
        return o.toString();
    }
}
