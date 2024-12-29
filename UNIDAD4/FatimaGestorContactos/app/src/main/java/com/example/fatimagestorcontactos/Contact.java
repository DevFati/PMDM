package com.example.fatimagestorcontactos;

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
}
