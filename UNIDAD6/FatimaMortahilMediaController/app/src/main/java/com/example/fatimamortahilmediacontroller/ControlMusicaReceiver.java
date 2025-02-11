package com.example.fatimamortahilmediacontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

public class ControlMusicaReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) { //metodo que se ejecuta cuando se recibe el evento de difusion
        String accion = intent.getAction(); // aqui obtiene la accion recibida del intent
        if (accion != null) { //verificamos que la accion no sea nula
            if (accion.equals("PAUSAR")) {  //comprueba si la accion recibida es "PAUSAR"
                if (MainActivity.mp != null && MainActivity.mp.isPlaying()) {  //verificamos si el mediaplayer no es nulo y esta reproduciendo
                    MainActivity.mp.pause(); //pausa la reproducion de la msica
                    MainActivity.ultimaPosicion = MainActivity.mp.getCurrentPosition(); // Guarda la posición

                }
            } else if (accion.equals("REANUDAR")) { //omprueba si la accion recibida es "REANUDAR"
                if (MainActivity.mp != null && MainActivity.mp.isPlaying()) {  //verifica si el mediaplayer no es nulo y esta reproduciendo
                    MainActivity.mp.seekTo(MainActivity.mp.getCurrentPosition()); // Mantiene la posición
                    MainActivity.mp.start(); //reanuda la reproduccion de la musica

                }
            }
        }
    }
}
