package org.example;

import java.util.*;
import java.util.regex.*;

public class AnalizadorLexico {

    private static Map<String, Token> tablaTokens = new HashMap<>();

    public static boolean cargarTabla(String rutaTokens) {
        try {
            tablaTokens = TokenTableLoader.cargarTokens(rutaTokens);
            return !tablaTokens.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            tablaTokens.clear();
            return false;
        }
    }

    public static List<TokenDetectado> analizarCodigo(String texto) {
        List<TokenDetectado> tokens = new ArrayList<>();

        if (tablaTokens.isEmpty()) {
            tokens.add(new TokenDetectado("ERROR", "No se pudo cargar la tabla de tokens.", 0, 0, 0));
            return tokens;
        }

        List<String> operadoresCompuestos = new ArrayList<>();
        for (String lexema : tablaTokens.keySet()) {
            if (lexema.length() > 1) operadoresCompuestos.add(Pattern.quote(lexema));
        }
        operadoresCompuestos.sort((a, b) -> Integer.compare(b.length(), a.length()));
        String patronesOperadores = String.join("|", operadoresCompuestos);

        String tokenPattern = String.format(
                "(%s)|" +                         // operadores compuestos
                        "(//.*$)|" +                      // comentario desde //
                        "(\"(\\\\.|[^\"\\\\])*\"?)|" +    // cadenas
                        "([a-zA-Z_][a-zA-Z0-9_]*)|" +     // identificadores o palabras reservadas
                        "([0-9]+\\.[0-9]+[a-zA-Z_]+)|" +  // error: decimal seguido de letras
                        "([0-9]+[a-zA-Z_]+)|" +           // error: entero seguido de letras
                        "([0-9]+\\.[0-9]+)|" +            // número decimal
                        "([0-9]+)|" +                     // número entero
                        "([{}();,+\\-*/=><!])|" +         // operadores simples
                        "(\\S)",                          // cualquier caracter no blanco no reconocido
                patronesOperadores.isEmpty() ? "" : patronesOperadores
        );

        Pattern pattern = Pattern.compile(tokenPattern, Pattern.MULTILINE);

        String[] lineas = texto.split("\n");
        int numLinea = 1;
        int indiceGlobal = 0;

        for (String linea : lineas) {
            Matcher matcher = pattern.matcher(linea);

            while (matcher.find()) {
                String lexema = matcher.group();
                int columna = matcher.start() + 1;
                int indice = indiceGlobal + matcher.start();

                if (lexema == null || lexema.isEmpty()) continue;

                if (lexema.startsWith("//")) {
                    tokens.add(new TokenDetectado("COM", lexema.trim(), numLinea, columna, indice));
                    break;
                }

                if (!operadoresCompuestos.isEmpty() &&
                        operadoresCompuestos.stream().anyMatch(op -> lexema.equals(op.replace("\\", "")))) {
                    Token t = tablaTokens.get(lexema);
                    tokens.add(new TokenDetectado(t.getCategoria(), t.getComponente(), numLinea, columna, indice));
                    continue;
                }

                if (lexema.startsWith("\"")) {
                    if (lexema.length() < 2 || !lexema.endsWith("\"")) {
                        tokens.add(new TokenDetectado("ERROR", lexema, numLinea, columna, indice));
                    } else {
                        tokens.add(new TokenDetectado("LIT_STR", lexema, numLinea, columna, indice));
                    }
                    continue;
                }

                if (tablaTokens.containsKey(lexema)) {
                    Token t = tablaTokens.get(lexema);
                    tokens.add(new TokenDetectado(t.getCategoria(), t.getComponente(), numLinea, columna, indice));
                }
                else if (lexema.matches("[0-9]+\\.[0-9]+")) {
                    tokens.add(new TokenDetectado("LIT_DEC", lexema, numLinea, columna, indice));
                }
                else if (lexema.matches("[0-9]+")) {
                    tokens.add(new TokenDetectado("LIT_INT", lexema, numLinea, columna, indice));
                }
                else if (lexema.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                    tokens.add(new TokenDetectado("ID", lexema, numLinea, columna, indice));
                }
                else if (lexema.matches("[0-9]+[a-zA-Z_]+") || lexema.matches("[0-9]+\\.[0-9]+[a-zA-Z_]+")) {
                    // número seguido de letras = error léxico
                    tokens.add(new TokenDetectado("ERROR", lexema, numLinea, columna, indice));
                }
                else if (lexema.matches("[{}();,+\\-*/=><!]")) {
                    Token t = tablaTokens.get(lexema);
                    if (t != null)
                        tokens.add(new TokenDetectado(t.getCategoria(), t.getComponente(), numLinea, columna, indice));
                    else
                        tokens.add(new TokenDetectado("ERROR", lexema, numLinea, columna, indice));
                }
                else {
                    tokens.add(new TokenDetectado("ERROR", lexema, numLinea, columna, indice));
                }
            }
            indiceGlobal += linea.length() + 1;
            numLinea++;
        }

        return tokens;
    }



}
