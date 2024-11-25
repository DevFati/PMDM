package com.example.fragmentosbiblioteca;

import java.io.Serializable;

public class Book implements Serializable {
    private String titulo;
    private String autor;
    private int año;
    private String descripcion;

    public Book(String titulo, String autor, int año, String descripcion) {
        this.titulo = titulo;
        this.autor = autor;
        this.año = año;
        this.descripcion = descripcion;
    }


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public int getAño() {
        return año;
    }

    public void setAño(int año) {
        this.año = año;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}