package org.tinywind.server.util.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.Arrays;
import java.util.List;

public class AbstractSinglePageExcel extends AbstractExcel {

    protected XSSFSheet sheet;
    protected int rownum;

    public AbstractSinglePageExcel() {
        super();
        this.sheet = workbook.createSheet();
    }

    public XSSFRow createRow() {
        return sheet.createRow(rownum++);
    }

    public XSSFRow addRow(CellStyle style, Object... columns) {
        return this.addRow(style, Arrays.asList(columns));
    }

    public XSSFRow addRow(CellStyle style, List<Object> columns) {
        XSSFRow row = this.createRow();
        int column = 0;
        for (Object c : columns) {
            Cell cell = row.createCell(column++);
            if (c instanceof Number) {
                cell.setCellValue(((Number) c).doubleValue());
            } else {
                cell.setCellValue(c.toString());
            }

            if (style != null)
                cell.setCellStyle(style);
        }
        return row;
    }

    public XSSFSheet getSheet() {
        return sheet;
    }

    public void setSheet(XSSFSheet sheet) {
        this.sheet = sheet;
        this.rownum = 0;
    }

    public int getRownum() {
        return rownum;
    }

    public void setRownum(int rownum) {
        this.rownum = rownum;
    }
}
