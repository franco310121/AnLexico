package org.example;

public class TokenDetectado {
    public String tipo;
    public String lexema;
    public int linea;
    public int columna;
    public int indice;

    public TokenDetectado(String tipo, String lexema, int linea, int columna, int indice) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.linea = linea;
        this.columna = columna;
        this.indice = indice;
    }
}

