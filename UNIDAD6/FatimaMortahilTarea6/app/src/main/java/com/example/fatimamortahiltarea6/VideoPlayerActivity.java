package com.example.fatimamortahiltarea6;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

//Clase que gestiona los videos
public class VideoPlayerActivity extends AppCompatActivity {
    private VideoView vistaVideo; //Componente donde mostraremos el video
    private MediaController controlador; //Controlador para el video
    private Button botonVolver;
    private boolean actividadActiva = true; // Controla si la actividad sigue activa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        vistaVideo = findViewById(R.id.vistaVideo);
        botonVolver = findViewById(R.id.botonVolver);
        //Obtenemosla URI del video que se envio desde la actividad anterior
        String uri = getIntent().getStringExtra("URI");
    //Configuramos el controlador multimedia
        controlador = new MediaController(this);
        controlador.setAnchorView(vistaVideo); //Ancla el controlador al VideoView
        controlador.setMediaPlayer(vistaVideo); // Vincula el controlador con el VideoView
        vistaVideo.setMediaController(controlador); //Estableceel controlador en el VideoView

        //Convierte la URI en un objeto Uri
        Uri uriParseada = Uri.parse(uri);
        String esquema = uriParseada.getScheme(); //Obtenemos el esquema de la URI

        if (esquema != null) { //Si la URI tiene un esquema válido, se asigna directamente
            vistaVideo.setVideoURI(uriParseada);
        } else { //Si no tiene esquema, se asume que es un recurso local que esta en "raw"
            int idRecurso = getResources().getIdentifier(uri, "raw", getPackageName());
            if (idRecurso != 0) { //Si el recurso existe, se obtiene su URI y se asigna al VideoView
                vistaVideo.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + idRecurso));
            }
        }
        //Listener que se activa cuando el video esta listo para sonar
        vistaVideo.setOnPreparedListener(mp -> {
            vistaVideo.start(); // Inicia el video
            controlador.show(0); //  muestra el controlador FIJO al iniciar el video
        });

        botonVolver.setOnClickListener(v -> finish()); //se cierra la actividad al darle al boton de volver

        actividadActiva = true; // Marca la actividad como activa
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        //  Solo que se vea  el controlador si la actividad sigue activa y el video está reproduciendo
        if (hasFocus && actividadActiva && controlador != null && vistaVideo.isPlaying()) {
            controlador.show(0); //el controlador multimedia sigue visible
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        actividadActiva = false; //la actividad se marca como inactiva

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
            controlador.hide(); //ocultamos el controlador
            controlador.setAnchorView(null); //  DESVINCULA EL CONTROLADOR
            controlador = null;
        }

        if (vistaVideo != null) {
            vistaVideo.stopPlayback(); //  DETENEMOOS LA REPRODUCCIÓN COMPLETAMENTE
        }
    }
}
