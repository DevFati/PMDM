package com.example.fragmentosbiblioteca;

import java.io.Serializable;

public class Book implements Serializable {
    private String titulo;
    private String autor;
    private int anio;
    private String descripcion;

    public Book(String titulo, String autor, int anio, String descripcion) {
        this.titulo = titulo;
        this.autor = autor;
        this.anio = anio;
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public int getAnio() {
        return anio;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

