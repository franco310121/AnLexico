package org.example;
import java.io.*;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static class Token {
        int codigo;
        String componente;
        String descripcion;

        public Token(int codigo, String componente, String descripcion) {
            this.codigo = codigo;
            this.componente = componente;
            this.descripcion = descripcion;
        }
    }

    static List<Token> tablaTokens = new ArrayList<>();
    static boolean huboError = false;
    static Set<Integer> lineasConErrores = new HashSet<>();

    public static void main(String[] args) {
        cargarTokensDesdeExcel("tabla_tokens.xlsx");

        try (BufferedReader br = new BufferedReader(new FileReader("programa.txt"))) {
            String linea;
            int lineaNum = 1;

            while ((linea = br.readLine()) != null) {
                System.out.println("Línea " + lineaNum + ": " + linea);
                analizarLinea(linea, lineaNum);
                lineaNum++;
            }

            // Resultado final
            if (huboError) {
                System.out.println("❌ Se encontraron errores léxicos en las siguientes líneas: " + lineasConErrores);
            } else {
                System.out.println("✅ Análisis completado sin errores léxicos.");
            }

        } catch (IOException e) {
            System.out.println("Error al leer archivo: " + e.getMessage());
        }
    }

    static void cargarTokensDesdeExcel(String nombreArchivo) {
        try (FileInputStream fis = new FileInputStream(nombreArchivo);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Omitir encabezado

                Cell cCodigo = row.getCell(0);
                Cell cComponente = row.getCell(1);
                Cell cDescripcion = row.getCell(2);

                if (cCodigo == null || cComponente == null || cDescripcion == null) continue;

                int codigo = (int) cCodigo.getNumericCellValue();
                String componente = cComponente.getStringCellValue();
                String descripcion = cDescripcion.getStringCellValue();

                tablaTokens.add(new Token(codigo, componente, descripcion));
            }

        } catch (Exception e) {
            System.out.println("Error al cargar el archivo Excel: " + e.getMessage());
        }
    }

    static void analizarLinea(String linea, int numeroLinea) {
        String[] palabras = linea.split("\\s+|(?=[(){}=+\\-*/])|(?<=[(){}=+\\-*/])");

        for (String palabra : palabras) {
            if (palabra.trim().isEmpty()) continue;

            Token token = buscarEnTabla(palabra);
            if (token != null) {
                System.out.println("TOKEN: [" + token.codigo + "] " + palabra + " → " + token.descripcion);
            } else if (palabra.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                System.out.println("TOKEN: [400] " + palabra + " → Identificador");
            } else if (palabra.matches("\\d+")) {
                System.out.println("TOKEN: [401] " + palabra + " → Número");
            } else {
                System.out.println("ERROR: '" + palabra + "' → Error léxico");
                huboError = true;
                lineasConErrores.add(numeroLinea);
            }
        }
        System.out.println();
    }

    static Token buscarEnTabla(String componente) {
        for (Token t : tablaTokens) {
            if (t.componente.equals(componente)) {
                return t;
            }
        }
        return null;
    }
}