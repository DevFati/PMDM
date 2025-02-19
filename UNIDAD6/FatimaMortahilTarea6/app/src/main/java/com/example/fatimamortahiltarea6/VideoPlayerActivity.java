package com.example.fatimamortahiltarea6;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayerActivity extends AppCompatActivity {
    private VideoView vistaVideo;
    private MediaController controlador;
    private Button botonVolver;
    private boolean actividadActiva = true; // Controla si la actividad sigue activa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        vistaVideo = findViewById(R.id.vistaVideo);
        botonVolver = findViewById(R.id.botonVolver);

        String uri = getIntent().getStringExtra("URI");

        controlador = new MediaController(this);
        controlador.setAnchorView(vistaVideo);
        controlador.setMediaPlayer(vistaVideo); // Vincula el controlador con el VideoView
        vistaVideo.setMediaController(controlador);

        Uri uriParseada = Uri.parse(uri);
        String esquema = uriParseada.getScheme();

        if (esquema != null) {
            vistaVideo.setVideoURI(uriParseada);
        } else {
            int idRecurso = getResources().getIdentifier(uri, "raw", getPackageName());
            if (idRecurso != 0) {
                vistaVideo.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + idRecurso));
            }
        }

        vistaVideo.setOnPreparedListener(mp -> {
            vistaVideo.start();
            controlador.show(0); //  muestra el controlador FIJO al iniciar el video
        });

        botonVolver.setOnClickListener(v -> finish());

        actividadActiva = true; // Marca la actividad como activa
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        //  Solo que se vea  el controlador si la actividad sigue activa y el video está reproduciendo
        if (hasFocus && actividadActiva && controlador != null && vistaVideo.isPlaying()) {
            controlador.show(0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        actividadActiva = false;

        //  Oculta el controlador al salir de la pantalla
        if (controlador != null) {
            controlador.hide();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        actividadActiva = false; // Marca la actividad como cerrada

        if (controlador != null) {
            controlador.hide();
            controlador.setAnchorView(null); //  DESVINCULA EL CONTROLADOR
            controlador = null;
        }

        if (vistaVideo != null) {
            vistaVideo.stopPlayback(); //  DETENEMOOS LA REPRODUCCIÓN COMPLETAMENTE
        }
    }
}
