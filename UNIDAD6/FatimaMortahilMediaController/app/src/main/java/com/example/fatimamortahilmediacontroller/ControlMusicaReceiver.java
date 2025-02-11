package com.example.fatimamortahilmediacontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

public class ControlMusicaReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String accion = intent.getAction();
        if (accion != null) {
            if (accion.equals("PAUSAR")) {
                if (MainActivity.mp != null && MainActivity.mp.isPlaying()) {
                    MainActivity.mp.pause();
                }
            } else if (accion.equals("DETENER")) {
                if (MainActivity.mp != null && MainActivity.mp.isPlaying()) {
                    MainActivity.mp.stop();
                    MainActivity.mp.reset();
                }
            }
        }
    }
}
