package com.nttdata.utils.database;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ExcelReader {

    private Workbook workbook;

    public ExcelReader(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(new File(filePath));
        this.workbook = new XSSFWorkbook(fis);
    }

    /**
     * Lee todos los datos de una hoja específica y los devuelve como un JSONArray.
     * La primera fila se utiliza como nombres de las columnas.
     *
     * @param sheetName Nombre de la hoja a leer.
     * @return JSONArray donde cada objeto representa una fila.
     */
    public JSONArray readSheetAsJson(String sheetName) {
        JSONArray data = new JSONArray();
        Sheet sheet = workbook.getSheet(sheetName);

        if (sheet == null) {
            throw new IllegalArgumentException("La hoja con nombre '" + sheetName + "' no existe.");
        }

        // Obtener la primera fila como nombres de las columnas
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new IllegalArgumentException("La hoja '" + sheetName + "' no tiene una fila de encabezado.");
        }

        int columnCount = headerRow.getLastCellNum();
        String[] columnNames = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            Cell cell = headerRow.getCell(i);
            columnNames[i] = cell != null ? getCellValueAsString(cell) : "Column" + i;
        }

        // Leer las filas restantes como datos
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue; // Saltar filas vacías
            }

            JSONObject jsonObject = new JSONObject();
            for (int j = 0; j < columnCount; j++) {
                String columnName = columnNames[j];
                Cell cell = row.getCell(j);
                String cellValue = cell != null ? getCellValueAsString(cell) : null;
                jsonObject.put(columnName, cellValue);
            }
            data.put(jsonObject);
        }

        return data;
    }

    /**
     * Obtiene el valor de una celda como String.
     *
     * @param cell Celda a leer.
     * @return Valor de la celda como String o null si está vacía.
     */
    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return null;
            default:
                return null;
        }
    }

    /**
     * Cierra el workbook para liberar recursos.
     *
     * @throws IOException Si ocurre un error al cerrar.
     */
    public void close() throws IOException {
        if (workbook != null) {
            workbook.close();
        }
    }
}
