package com.example.gestorcontactosfatima;

public class Contact {
    private String nombre;
    private String telefono;

    public Contact(String nombre, String telefono) {
        this.nombre = nombre;
        this.telefono = telefono;
    }


    public String getTelefono() {
        return telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
