package org.tinywind.server.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.tinywind.server.Application;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class DependencyProposalCompatibilityTest {
    private final ObjectMapper mapper = new Application().objectMapper(false);

    @Test void roundTripsVavrListsMapsOptionsAndTuples() throws Exception {
        List<String> list = List.of("first", "second");
        assertEquals(list, mapper.readValue(mapper.writeValueAsString(list), new TypeReference<List<String>>() {}));
        Map<String, Integer> map = HashMap.of("count", 2);
        assertEquals(map, mapper.readValue(mapper.writeValueAsString(map), new TypeReference<Map<String, Integer>>() {}));
        Option<String> option = Option.of("value");
        assertEquals(option, mapper.readValue(mapper.writeValueAsString(option), new TypeReference<Option<String>>() {}));
        Tuple2<String, Integer> tuple = Tuple.of("value", 2);
        assertEquals(tuple, mapper.readValue(mapper.writeValueAsString(tuple), new TypeReference<Tuple2<String, Integer>>() {}));
    }

    @Test void roundTripsStyledExcelWorkbook() throws Exception {
        byte[] bytes;
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Report");
            var row = sheet.createRow(0);
            row.createCell(0).setCellValue("Sample");
            var number = row.createCell(1);
            number.setCellValue(42.5);
            var style = workbook.createCellStyle();
            style.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
            number.setCellStyle(style);
            workbook.write(out);
            bytes = out.toByteArray();
        }
        assertTrue(bytes.length > 0);
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            var row = workbook.getSheet("Report").getRow(0);
            assertEquals("Sample", row.getCell(0).getStringCellValue());
            assertEquals(42.5, row.getCell(1).getNumericCellValue());
            assertEquals("0.00", row.getCell(1).getCellStyle().getDataFormatString());
        }
    }
}
