package com.example.fatimamortahiltarea6;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayerActivity extends AppCompatActivity {
    private VideoView vistaVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        vistaVideo = findViewById(R.id.vistaVideo);
        String uri = getIntent().getStringExtra("URI");

        if (uri.startsWith("http")) {
            // Si es un video en streaming
            vistaVideo.setVideoURI(Uri.parse(uri));
        } else {
            // Si es un video local en raw/
            int idRecurso = getResources().getIdentifier(uri, "raw", getPackageName());
            if (idRecurso != 0) {
                vistaVideo.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + idRecurso));
            } else {
                Toast.makeText(this, "No se encontró el archivo de video", Toast.LENGTH_SHORT).show();
            }
        }

        vistaVideo.setMediaController(new MediaController(this));
        vistaVideo.start();
    }

}
