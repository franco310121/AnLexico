package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class TokenTableLoader {

    public static Map<String, Token> cargarTokens(String rutaExcel) {
        Map<String, Token> tablaTokens = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(new File(rutaExcel));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet hoja = workbook.getSheetAt(0);

            for (Row fila : hoja) {
                if (fila.getRowNum() == 0) continue; // Saltar cabecera

                Cell celdaCodigo = fila.getCell(0);
                Cell celdaComponente = fila.getCell(1);
                Cell celdaCategoria = fila.getCell(2);
                Cell celdaDescripcion = fila.getCell(3);

                // Validar que no haya celdas nulas
                if (celdaCodigo == null || celdaComponente == null || celdaCategoria == null || celdaDescripcion == null) {
                    continue; // Saltar fila incompleta
                }

                int codigo = (int) celdaCodigo.getNumericCellValue();
                String comp = celdaComponente.getStringCellValue().trim();
                String cat = celdaCategoria.getStringCellValue().trim();
                String desc = celdaDescripcion.getStringCellValue().trim();

                tablaTokens.put(comp, new Token(codigo, comp, cat, desc));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return tablaTokens;
    }
}
