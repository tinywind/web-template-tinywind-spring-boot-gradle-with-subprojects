package org.tinywind.server.util.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractExcel {

    protected XSSFWorkbook workbook;
    protected Map<String, SimpleDateFormat> dateFormats = new HashMap<>();
    protected Map<String, CellStyle> cellStyles = new HashMap<>();

    public AbstractExcel() {
        this.workbook = new XSSFWorkbook();
    }

    protected SimpleDateFormat getDateFormat(String pattern) {
        SimpleDateFormat sdf = dateFormats.get(pattern);
        if (sdf == null) {
            sdf = new SimpleDateFormat(pattern);
            dateFormats.put(pattern, sdf);
        }
        return sdf;
    }

    protected CellStyle getCurrencyStyle() {
        return getCellStyle(CellStyleFormat.CURRENCY);
    }

    protected CellStyle getPercentageStyle() {
        return getCellStyle(CellStyleFormat.PERCENTAGE);
    }

    protected CellStyle getCellStyle(CellStyleFormat format) {
        return this.getCellStyle(format.getPattern());
    }

    protected CellStyle getCellStyle(String format) {
        CellStyle cellStyle = cellStyles.get(format);
        if (cellStyle == null) {
            cellStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            cellStyle.setDataFormat(dataFormat.getFormat(format));
            cellStyles.put(format, cellStyle);
        }
        return cellStyle;
    }

    public Cell setCellValue(Row row, int column, Date value, String pattern) {
        return this.setCellValue(row, column, value, pattern, null);
    }

    public Cell setCellValue(Row row, int column, Date value, String pattern, String defaultValue) {
        Cell cell = row.createCell(column);
        if (!StringUtils.hasText(pattern) && !StringUtils.hasText(defaultValue)) {
            cell.setCellValue(value);
        } else if (value == null && StringUtils.hasText(defaultValue)) {
            cell.setCellValue(defaultValue);
        } else if (value == null) {
            cell.setCellValue((String) null);
        } else {
            cell.setCellValue(this.getDateFormat(pattern).format(value));
        }
        return cell;
    }

    public Cell setCellValue(Row row, int column, Number value) {
        return this.setCellValue(row, column, value != null ? value.doubleValue() : 0d);
    }

    public Cell setCellValue(Row row, int column, double value) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        return cell;
    }

    public Cell setCellValue(Row row, int column, boolean value) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        return cell;
    }

    public Cell setCellValue(Row row, int column, String value) {
        if (value == null) {
            value = "";
        }
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        return cell;
    }

    public void generator(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachement; filename=\"" + java.net.URLEncoder.encode(fileName, "UTF-8") + ".xlsx\";charset=\"UTF-8\"");
        this.generator(response.getOutputStream());
    }

    public void generator(OutputStream os) {
        try {
            workbook.write(os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (os != null) try {
                os.close();
            } catch (Exception e) {
            }
            try {
                workbook.close();
            } catch (IOException e) {
            }
        }
    }

}
