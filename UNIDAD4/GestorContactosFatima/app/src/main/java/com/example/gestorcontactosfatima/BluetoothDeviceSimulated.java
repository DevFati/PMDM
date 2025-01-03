package com.example.gestorcontactosfatima;

public class BluetoothDeviceSimulated {
    private String nombre;
    private String direccion;


    public BluetoothDeviceSimulated(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
    }


    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }
}
