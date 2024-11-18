package com.example.prc2_fatimamortahil;

public class Casilla  {
    private int estado; // Representa el estado de la casilla (0: Oculta, 1: Descubierta por el usuario, 2: Marcada, 3:Descubierta al perder, 4: Mina pulsada)
    private boolean mina; // Indica si la casilla tiene una mina
    private int valor; // valor de la celda -1 si es mina y si no el numero de minas alrededor

    public Casilla() {
        this.estado = 0; // Inicialmente oculta
        this.mina = false; //Comprobamos si la casilla tiene una mina, por defecto marcamos false, es decir, que no tiene una mina.
        this.valor = 0;
    }

    // Métodos geters y seters
    public void setEstado(int nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public int getEstado() {
        return estado;
    }

    public void setMina(boolean esMina) {
        this.mina = esMina;
        if(esMina){
            this.valor=-1; //Si es mina, el valor seria -1
        }
    }

    public boolean esMina() {
        return mina;
    }

    public void setValor(int cantidad) {
        this.valor = cantidad;

    }

    public int getValor() {
        return valor;
    }
}

