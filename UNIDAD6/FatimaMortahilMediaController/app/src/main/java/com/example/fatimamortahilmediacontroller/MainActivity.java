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
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import android.Manifest;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    private TextView txtTiempo, txtTiempoRestante;
    private Button botonDetener, botonSeleccionar, botonSiguiente, botonAnterior, botonStop, botonVerLista;
    private SharedPreferences preferencias;
    private ArrayList<Uri> listaUrisCanciones;
    private ArrayList<String> listaNombresCanciones;
    private int indiceCancionActual = 0;
    private static final int SOLICITUD_SELECCION_AUDIO = 1;
    public static int ultimaPosicion;
    public static MediaController mc;
    public static MediaPlayer mp;
    private Handler h;
    private static final String ID_CANAL = "CanalReproductorMusica";
    private NotificationManager gestor;
    private static final int ID_NOTIFICACION = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Verificar y solicitar permisos de almacenamiento si es necesario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        preferencias = getSharedPreferences("PreferenciasMusica", MODE_PRIVATE);
        ultimaPosicion = preferencias.getInt("UltimaPosicion", 0);

        botonSeleccionar = findViewById(R.id.btnSeleccionar);
        botonDetener = findViewById(R.id.btnDetener);
        botonSiguiente = findViewById(R.id.btnSiguiente);
        botonAnterior = findViewById(R.id.btnAnterior);
        botonStop = findViewById(R.id.btnStop);
        botonVerLista = findViewById(R.id.btnVerLista);
        listaNombresCanciones = new ArrayList<>();
        listaUrisCanciones = new ArrayList<>();
        cargarListaCanciones();

        txtTiempo = findViewById(R.id.txtTiempo);
        txtTiempoRestante = findViewById(R.id.txtTiempoRestante);
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
        botonStop.setOnClickListener(v -> pararMusica());
        botonVerLista.setOnClickListener(v -> mostrarListaCanciones());
        crearCanalNotificacion();


    }





    private void mostrarListaCanciones() {
        if (listaNombresCanciones.isEmpty()) {
            Toast.makeText(this, "No hay canciones en la lista", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] nombresCanciones = listaNombresCanciones.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lista de Reproducción")
                .setItems(nombresCanciones, (dialog, which) -> {
                    indiceCancionActual = which;
                    reproducirCancionActual();
                })
                .setNegativeButton("Cerrar", (dialog, which) -> dialog.dismiss())
                .show();
    }



    private void pararMusica() {
        if (mp.isPlaying()) {
            mp.stop();
            ultimaPosicion=mp.getCurrentPosition();
            try {
                mp.prepare(); // Restablecer el MediaPlayer para poder volver a reproducir
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        Intent intentPausa = new Intent(this, ControlMusicaReceiver.class);
        intentPausa.setAction("PAUSAR");
        PendingIntent pausaIntent = PendingIntent.getBroadcast(this, 0, intentPausa, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent intentReanudar = new Intent(this, ControlMusicaReceiver.class);
        intentReanudar.setAction("REANUDAR");
        PendingIntent reanudarIntent = PendingIntent.getBroadcast(this, 1, intentReanudar, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder constructor = new NotificationCompat.Builder(this, ID_CANAL)
                .setSmallIcon(R.drawable.music_player)
                .setContentTitle("Reproductor de Música")
                .setContentText("Reproduciendo: " + listaUrisCanciones.get(indiceCancionActual))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .addAction(R.drawable.pause, "Pausar", pausaIntent)
                .addAction(R.drawable.stop, "Reanudar", reanudarIntent);

        gestor.notify(ID_NOTIFICACION, constructor.build());
    }




    private void cargarListaCanciones() {
        listaUrisCanciones.clear();
        listaNombresCanciones.clear();


        listaUrisCanciones.add(Uri.parse("android.resource://" + getPackageName() + "/raw/eltiempopasara"));
        listaNombresCanciones.add("El Tiempo Pasará");
        listaUrisCanciones.add(Uri.parse("android.resource://" + getPackageName() + "/raw/mrsandman"));
        listaNombresCanciones.add("Mr. Sandman");
        listaUrisCanciones.add(Uri.parse("android.resource://" + getPackageName() + "/raw/someonelikeyou"));
        listaNombresCanciones.add("Someone Like You");

    }




    private void reproducirCancionActual() {
        try {

            if(mp.isPlaying()){
                mp.stop();

            }
            mp.release();
            mp.setDataSource(this, listaUrisCanciones.get(indiceCancionActual));
            mp.prepare();

                mp.start();

                mostrarNotificacion();
                actualizarTiempo();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void siguienteCancion() {
        if (!listaUrisCanciones.isEmpty()) {
            indiceCancionActual = (indiceCancionActual + 1) % listaUrisCanciones.size();
            reproducirCancionActual();
        }
    }

    private void anteriorCancion() {
        if (!listaUrisCanciones.isEmpty()) {
            indiceCancionActual = (indiceCancionActual - 1 + listaUrisCanciones.size()) % listaUrisCanciones.size();
            reproducirCancionActual();
        }
    }

    private void detenerMusica() {
        if (mp.isPlaying()) {
            mp.pause();

        }
        mp.seekTo(0); // Reinicia la canción a 00:00
        ultimaPosicion=mp.getCurrentPosition();
        actualizarTiempo();
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
            String nombreCancion = obtenerNombreCancion(uriAudio);
            if (listaNombresCanciones.contains(nombreCancion)) {
                Toast.makeText(this, "Canción ya existente en lista", Toast.LENGTH_SHORT).show();
                return;
            }

            listaUrisCanciones.add(uriAudio);
            listaNombresCanciones.add(nombreCancion);
            indiceCancionActual = listaUrisCanciones.size() - 1;
            reproducirCancionActual();
        }
    }

    private String obtenerNombreCancion(Uri uri) {
        String nombre = "Cancion";
        Cursor cursor = getContentResolver().query(uri,
                new String[]{MediaStore.MediaColumns.DISPLAY_NAME},
                null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                if (index != -1) {
                    nombre = cursor.getString(index);
                }
            }
            cursor.close();
        }
        return nombre;
    }


    private void actualizarTiempo() {
        if (h == null) return;

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mp != null && mp.isPlaying()) {
                    int posicionActual = mp.getCurrentPosition() / 1000;
                    int duracionTotal = mp.getDuration() / 1000;
                    int tiempoRestante = duracionTotal - posicionActual;

                    String tiempoFormato = String.format(Locale.getDefault(), "%02d:%02d / %02d:%02d",
                            posicionActual / 60, posicionActual % 60,
                            duracionTotal / 60, duracionTotal % 60);
                    txtTiempo.setText(tiempoFormato);

                    String tiempoRestanteFormato = String.format(Locale.getDefault(), "Tiempo restante: %02d:%02d",
                            tiempoRestante / 60, tiempoRestante % 60);
                    txtTiempoRestante.setText(tiempoRestanteFormato);

                    h.postDelayed(this, 1000);
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
            ultimaPosicion=mp.getCurrentPosition();
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
                Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mp != null) {
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putInt("UltimaPosicion", mp.getCurrentPosition());
            editor.apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mp.isPlaying()){
            ultimaPosicion=preferencias.getInt("UltimaPosicion",0);
            mp.seekTo(ultimaPosicion);
            mp.start();
            actualizarTiempo();

        }
    }



}
