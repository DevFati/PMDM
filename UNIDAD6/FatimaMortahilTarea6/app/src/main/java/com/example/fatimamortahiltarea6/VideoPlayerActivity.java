package com.example.fatimamortahiltarea6;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayerActivity extends AppCompatActivity {
    private VideoView vistaVideo;
    private MediaController controlador;
    private Button botonVolver;
    private boolean actividadActiva = true; // Variable para saber si la actividad sigue activa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        vistaVideo = findViewById(R.id.vistaVideo);
        String uri = getIntent().getStringExtra("URI");
        botonVolver = findViewById(R.id.botonVolver);

        controlador = new MediaController(this);
        controlador.setAnchorView(vistaVideo);
        vistaVideo.setMediaController(controlador);

        Uri uriParseada = Uri.parse(uri);
        String esquema = uriParseada.getScheme(); // Obtiene el esquema de la URI

        if (esquema != null) {
            // Si es un video en streaming
            vistaVideo.setVideoURI(uriParseada);
        } else {
            // Si es un video local en raw/
            int idRecurso = getResources().getIdentifier(uri, "raw", getPackageName());
            if (idRecurso != 0) {
                vistaVideo.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + idRecurso));
            } else {
                Toast.makeText(this, "No se encontró el archivo de video", Toast.LENGTH_SHORT).show();
            }
        }

        vistaVideo.setOnPreparedListener(mp -> vistaVideo.start());

        botonVolver.setOnClickListener(v -> finish());

        actividadActiva=true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && controlador!=null && actividadActiva ) {
            controlador.show(0); //
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        actividadActiva = false;

// Detener la reproducción del video si aún está en curso
        if (vistaVideo != null) {
            vistaVideo.suspend();
        }
        // Asegurar que el MediaController ya no intente mostrarse
        if (controlador != null) {
            controlador.hide();
            controlador.setAnchorView(null);
            controlador = null;
        }



    }

    @Override
    protected void onStop(){

        super.onStop();

        actividadActiva = false;

    }


}
