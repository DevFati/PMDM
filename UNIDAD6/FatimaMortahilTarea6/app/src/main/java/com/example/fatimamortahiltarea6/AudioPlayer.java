package com.example.fatimamortahiltarea6;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.MediaController;
import android.view.View;

public class AudioPlayer {
    private MediaPlayer reproductor;
    private MediaController controlador;
    private View contenedorReproductor;
    private Context contexto;

    public AudioPlayer(Context contexto, View contenedorReproductor) {
        this.contexto = contexto;
        this.contenedorReproductor = contenedorReproductor;
        this.reproductor = new MediaPlayer();
        this.controlador = new MediaController(contexto);
        this.controlador.setAnchorView(contenedorReproductor);
    }

    public void reproducirAudio(int idRecurso) {
        if (reproductor.isPlaying()) {
            detenerAudio();
        }
        reproductor = MediaPlayer.create(contexto, idRecurso);
        if (reproductor != null) {
            reproductor.start();
            contenedorReproductor.setVisibility(View.VISIBLE);
            configurarControlador();
        }
    }

    private void configurarControlador() {
        controlador.setMediaPlayer(new MediaController.MediaPlayerControl() {
            @Override public void start() { reproductor.start(); }
            @Override public void pause() { reproductor.pause(); }
            @Override public int getDuration() { return reproductor.getDuration(); }
            @Override public int getCurrentPosition() { return reproductor.getCurrentPosition(); }
            @Override public void seekTo(int pos) { reproductor.seekTo(pos); }
            @Override public boolean isPlaying() { return reproductor.isPlaying(); }
            @Override public int getBufferPercentage() { return 0; }
            @Override public boolean canPause() { return true; }
            @Override public boolean canSeekBackward() { return true; }
            @Override public boolean canSeekForward() { return true; }
            @Override public int getAudioSessionId() { return reproductor.getAudioSessionId(); }
        });
        controlador.show();
    }

    public void detenerAudio() {
        if (reproductor != null && reproductor.isPlaying()) {
            reproductor.stop();
            reproductor.reset();
            contenedorReproductor.setVisibility(View.GONE);
        }
    }

    public void liberarRecursos() {
        if (reproductor != null) {
            reproductor.release();
            reproductor = null;
        }
    }
}
