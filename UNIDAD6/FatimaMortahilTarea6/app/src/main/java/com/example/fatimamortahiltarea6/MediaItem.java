package com.example.fatimamortahiltarea6;

public class MediaItem {
    private String nombre;
    private String descripcion;
    private int tipo;
    private String uri;
    private String imagen;

    public MediaItem(String nombre, String descripcion, int tipo, String uri, String imagen) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.uri = uri;
        this.imagen = imagen;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public String obtenerDescripcion() {
        return descripcion;
    }

    public int obtenerTipo() {
        return tipo;
    }

    public String obtenerUri() {
        return uri;
    }

    public String obtenerImagen() {
        return imagen;
    }
}
