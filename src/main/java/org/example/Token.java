package org.example;

public class Token {
    private final int codigo;
    private final String componente;
    private final String categoria;
    private final String descripcion;

    public Token(int codigo, String componente, String categoria, String descripcion) {
        this.codigo = codigo;
        this.componente = componente;
        this.categoria = categoria;
        this.descripcion = descripcion;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getComponente() {
        return componente;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return String.format("[%d, %s, %s]", codigo, componente, categoria);
    }
}

