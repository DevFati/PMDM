package edu.pmdm.actividadsmstocontact_fatima;

import android.graphics.Bitmap;

public class Contacto {
    private String nombre;
    private String numero;
    private Bitmap foto;

    public Contacto(String nombre, Bitmap foto, String numero) {
        this.nombre = nombre;
        this.foto = foto;
        this.numero = numero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public Contacto(String nombre, String numero) {
        this.nombre = nombre;
        this.numero = numero;
    }
}
