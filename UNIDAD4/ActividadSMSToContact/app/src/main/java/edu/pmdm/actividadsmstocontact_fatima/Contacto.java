package edu.pmdm.actividadsmstocontact_fatima;

public class Contacto {
    private String nombre;
    private String numero;
    private String foto;

    public Contacto(String nombre, String foto, String numero) {
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

    public String getFoto() {
        return foto;
    }

    public Contacto(String nombre, String numero) {
        this.nombre = nombre;
        this.numero = numero;
    }
}
