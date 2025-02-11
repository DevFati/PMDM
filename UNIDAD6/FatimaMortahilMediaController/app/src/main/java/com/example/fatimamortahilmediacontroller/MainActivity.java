package com.example.fatimamortahilmediacontroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import android.Manifest;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    private TextView txtTiempo;
    private Button botonDetener, botonSeleccionar, botonSiguiente, botonAnterior;
    private SharedPreferences preferencias;
    private ArrayList<Uri> listaCanciones;
    private int indiceCancionActual = 0;
    private static final int SOLICITUD_SELECCION_AUDIO = 1;
    private int ultimaPosicion;
    public static MediaController mc;
    public static MediaPlayer mp;
    private Handler h;
    private static final String ID_CANAL = "CanalReproductorMusica";
    private NotificationManager gestor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Verificar y solicitar permisos de almacenamiento si es necesario
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) { // API 33+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 1);
            }
        }

        preferencias = getSharedPreferences("PreferenciasMusica", MODE_PRIVATE);
        ultimaPosicion = preferencias.getInt("UltimaPosicion", 0);

        txtTiempo = findViewById(R.id.txtTiempo);
        botonSeleccionar = findViewById(R.id.btnSeleccionar);
        botonDetener = findViewById(R.id.btnDetener);
        botonSiguiente = findViewById(R.id.btnSiguiente);
        botonAnterior = findViewById(R.id.btnAnterior);

        listaCanciones = new ArrayList<>();
        cargarListaCanciones();


        mp = new MediaPlayer();
        h=new Handler();
        mc = new MediaController(this);
        mc.setMediaPlayer(this);
        mc.setAnchorView(findViewById(R.id.constraintLayout));




        botonSeleccionar.setOnClickListener(v -> seleccionarAudio());
        botonDetener.setOnClickListener(v -> detenerMusica());
        botonSiguiente.setOnClickListener(v -> siguienteCancion());
        botonAnterior.setOnClickListener(v -> anteriorCancion());
        findViewById(R.id.constraintLayout).post(this::reproducirCancionActual);
        gestor = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        crearCanalNotificacion();


    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mc.show(0); // Ahora MediaController solo se muestra cuando la ventana está lista
        }
    }



    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    ID_CANAL,
                    "Notificaciones Reproductor de Música",
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager gestor = getSystemService(NotificationManager.class);
            gestor.createNotificationChannel(canal);
        }
    }

    private void mostrarNotificacion() {
        NotificationCompat.Builder constructor = new NotificationCompat.Builder(this, ID_CANAL)
                .setSmallIcon(R.drawable.music_player)
                .setContentTitle("Reproductor de Música")
                .setContentText("Reproduciendo: " + listaCanciones.get(indiceCancionActual).getLastPathSegment())
                .setPriority(NotificationCompat.PRIORITY_LOW);
        gestor.notify(1, constructor.build());
    }




    private void cargarListaCanciones() {
        Set<String> cancionesGuardadas = preferencias.getStringSet("ListaCanciones", new HashSet<>());
        for (String uriString : cancionesGuardadas) {
            listaCanciones.add(Uri.parse(uriString));
        }
        listaCanciones.add(Uri.parse("android.resource://" + getPackageName() + "/raw/eltiempopasara"));
        listaCanciones.add(Uri.parse("android.resource://" + getPackageName() + "/raw/mrsandman"));
        listaCanciones.add(Uri.parse("android.resource://" + getPackageName() + "/raw/someonelikeyou"));
    }

    private void guardarListaCanciones() {
        Set<String> cancionesAguardar = new HashSet<>();
        for (Uri uri : listaCanciones) {
            cancionesAguardar.add(uri.toString());
        }
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putStringSet("ListaCanciones", cancionesAguardar);
        editor.apply();
    }

    private void reproducirCancionActual() {
        try {
            mp.reset();
            mp.setDataSource(this, listaCanciones.get(indiceCancionActual));
            mp.prepare();

                mp.start();

                mostrarNotificacion();
                actualizarTiempo();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void siguienteCancion() {
        if (!listaCanciones.isEmpty()) {
            indiceCancionActual = (indiceCancionActual + 1) % listaCanciones.size();
            reproducirCancionActual();
        }
    }

    private void anteriorCancion() {
        if (!listaCanciones.isEmpty()) {
            indiceCancionActual = (indiceCancionActual - 1 + listaCanciones.size()) % listaCanciones.size();
            reproducirCancionActual();
        }
    }

    private void detenerMusica() {
        if (mp.isPlaying()) {
            mp.stop();
            mp.reset();

            gestor.cancel(1);
        }
    }

    private void seleccionarAudio() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        try {
            startActivityForResult(intent, SOLICITUD_SELECCION_AUDIO);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "No se encontró una aplicación para seleccionar audio", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SOLICITUD_SELECCION_AUDIO && resultCode == RESULT_OK && data != null) {
            Uri uriAudio = data.getData();
            listaCanciones.add(uriAudio);
            guardarListaCanciones();
            indiceCancionActual = listaCanciones.size() - 1;
            reproducirCancionActual();
        }
    }

    private void mostrarDialogoAgregarCancion(Uri uriAudio) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar a la lista de reproducción")
                .setMessage("¿Quieres agregar esta canción a la lista de reproducción?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    listaCanciones.add(uriAudio);
                    guardarListaCanciones();
                    Toast.makeText(this, "Canción agregada a la lista", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    try {
                        mp.reset();
                        mp.setDataSource(MainActivity.this, uriAudio);
                        mp.prepare();
                        mp.start();
                        mc.show();
                        actualizarTiempo();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .show();
    }

    private void actualizarTiempo() {
        if (h == null) return; // Evita `NullPointerException` si el `Handler` no está inicializado

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mp != null && mp.isPlaying()) {
                    int posicionActual = mp.getCurrentPosition() / 1000;
                    int duracionTotal = mp.getDuration() / 1000;

                    @SuppressLint("DefaultLocale") String tiempoFormato = String.format("%02d:%02d / %02d:%02d",
                            posicionActual / 60, posicionActual % 60,
                            duracionTotal / 60, duracionTotal % 60);

                    txtTiempo.setText(tiempoFormato);
                    actualizarTiempo();
                }
            }
        }, 1000);
    }


    @Override
    public void start() {
        if (!mp.isPlaying()) {
            mp.seekTo(ultimaPosicion);
            mp.start();
            actualizarTiempo();
        }
    }

    @Override
    public void pause() {
        if (mp.isPlaying()) {
            mp.pause();
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putInt("UltimaPosicion", mp.getCurrentPosition());
            editor.apply();
        }
    }

    @Override
    public int getDuration() {
        return mp.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mp.getCurrentPosition();
    }

    @Override
    public void seekTo(int i) {
        mp.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return mp.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return mp.getAudioSessionId();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
