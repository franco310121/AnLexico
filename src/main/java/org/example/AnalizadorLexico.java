package org.example;

import java.io.*;
import java.util.*;

public class AnalizadorLexico {

    public static void main(String[] args) {
        String rutaTokens = "tabla_tokens.xlsx";
        String rutaEntrada = "programa.txt";

        Map<String, Token> tablaTokens = TokenTableLoader.cargarTokens(rutaTokens);

        if (tablaTokens.isEmpty()) {
            System.out.println("Error: No se pudo cargar la tabla de tokens.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(rutaEntrada))) {
            String linea;
            int numLinea = 1;

            while ((linea = br.readLine()) != null) {
                System.out.println("Línea " + numLinea + ": " + linea);

                String[] lexemas = linea.split("\\s+|(?=[{}();,+\\-*/=><!])|(?<=[{}();,+\\-*/=><!])");
                for (String lexema : lexemas) {
                    if (lexema.isBlank()) continue;

                    Token token = tablaTokens.get(lexema);
                    if (token != null) {
                        System.out.println("  → " + token);
                    } else if (lexema.matches("[0-9]+")) {
                        System.out.println("  → [LIT_INT, " + lexema + "]");
                    } else if (lexema.matches("[0-9]+\\.[0-9]+")) {
                        System.out.println("  → [LIT_DEC, " + lexema + "]");
                    } else if (lexema.matches("\".*\"")) {
                        System.out.println("  → [LIT_STR, " + lexema + "]");
                    } else if (lexema.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                        System.out.println("  → [ID, " + lexema + "]");
                    } else {
                        System.out.println("  → [ERROR, " + lexema + "]");
                    }
                }

                numLinea++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
