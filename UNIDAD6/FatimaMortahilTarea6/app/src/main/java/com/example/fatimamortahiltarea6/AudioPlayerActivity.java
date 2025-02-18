package com.example.fatimamortahiltarea6;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AudioPlayerActivity extends AppCompatActivity {
    private MediaPlayer reproductor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        String uri = getIntent().getStringExtra("URI");
        int idRecurso = getResources().getIdentifier(uri, "raw", getPackageName());

        if (idRecurso != 0) {
            reproductor = MediaPlayer.create(this, idRecurso);
            reproductor.start();
        } else {
            Toast.makeText(this, "No se encontró el archivo de audio", Toast.LENGTH_SHORT).show();
        }
    }
}
