package com.example.fatimamortahiltarea6;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AudioPlayerActivity extends AppCompatActivity {
    private MediaPlayer reproductor; //Declaramos un objeto MediaPlayer para manejar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        String uri = getIntent().getStringExtra("URI"); //Obtenemos la URI del archivo de audio que se pasa desde otra actividad
        int idRecurso = getResources().getIdentifier(uri, "raw", getPackageName());
        //Buscamos el ID del recurso en la carpeta "res/raw" usando el nombre recibido en la URI
        if (idRecurso != 0) {
            reproductor = MediaPlayer.create(this, idRecurso); //creamos el nuevo Mediaplayer con el recurso encontrado
            reproductor.start(); //Iniciamos el audio
        } else {
            Toast.makeText(this, "No se encontró el archivo de audio", Toast.LENGTH_SHORT).show();
        }
    }
}
