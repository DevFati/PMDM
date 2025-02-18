package com.example.fatimamortahiltarea6;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.widget.MediaController;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView listaRecursos;
    private MediaAdapter adaptadorMedios;
    private List<MediaItem> listaMedios = new ArrayList<>();
    private List<MediaItem> listaFiltrada = new ArrayList<>();
    private int filtroSeleccionado = 0; // 0 = Todos, 1 = Audio, 2 = Video, 3 = Streaming

    private MediaPlayer reproductor;
    private MediaController controlador;
    private ViewGroup contenedorReproductor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaRecursos = findViewById(R.id.listaRecursos);
        listaRecursos.setLayoutManager(new LinearLayoutManager(this));

        adaptadorMedios = new MediaAdapter(listaFiltrada, this, this::iniciarReproduccionAudio);
        listaRecursos.setAdapter(adaptadorMedios);

        contenedorReproductor = findViewById(R.id.contenedorReproductor);
        controlador = new MediaController(this);

        cargarMediosDesdeJson();
        cargarFiltro();
    }

    private void cargarMediosDesdeJson() {
        try {
            InputStream entrada = getAssets().open("recursosList.json");
            int tamaño = entrada.available();
            byte[] buffer = new byte[tamaño];
            entrada.read(buffer);
            entrada.close();
            String json = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("recursos_list");
            listaMedios.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                listaMedios.add(new MediaItem(
                        obj.getString("nombre"),
                        obj.getString("descripcion"),
                        obj.getInt("tipo"),
                        obj.getString("URI"),
                        obj.getString("imagen")));
            }
            aplicarFiltro();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void iniciarReproduccionAudio(String uri) {
        if (reproductor != null) {
            reproductor.release();
        }

        int idRecurso = getResources().getIdentifier(uri, "raw", getPackageName());
        reproductor = MediaPlayer.create(this, idRecurso);
        reproductor.start();

        controlador.setAnchorView(contenedorReproductor);
        controlador.setMediaPlayer(new MediaController.MediaPlayerControl() {
            @Override public void start() { if (reproductor != null) reproductor.start(); }
            @Override public void pause() { if (reproductor != null) reproductor.pause(); }
            @Override public int getDuration() { return reproductor != null ? reproductor.getDuration() : 0; }
            @Override public int getCurrentPosition() { return reproductor != null ? reproductor.getCurrentPosition() : 0; }
            @Override public void seekTo(int pos) { if (reproductor != null) reproductor.seekTo(pos); }
            @Override public boolean isPlaying() { return reproductor != null && reproductor.isPlaying(); }
            @Override public int getBufferPercentage() { return 0; }
            @Override public boolean canPause() { return true; }
            @Override public boolean canSeekBackward() { return true; }
            @Override public boolean canSeekForward() { return true; }
            @Override public int getAudioSessionId() { return reproductor != null ? reproductor.getAudioSessionId() : 0; }
        });

        controlador.show(0);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            controlador.show(2000); //
        }
    }

    private void aplicarFiltro() {
        listaFiltrada.clear();
        for (MediaItem medio : listaMedios) {
            if (filtroSeleccionado == 0 || medio.obtenerTipo() == filtroSeleccionado - 1) {
                listaFiltrada.add(medio);
            }
        }
        adaptadorMedios.notifyDataSetChanged();
    }

    private void cargarFiltro() {
        SharedPreferences prefs = getSharedPreferences("Preferencias", MODE_PRIVATE);
        filtroSeleccionado = prefs.getInt("Filtro", 0);
        aplicarFiltro();
    }

    private void guardarFiltro() {
        SharedPreferences prefs = getSharedPreferences("Preferencias", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Filtro", filtroSeleccionado);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filtro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_filtro) {
            mostrarDialogoFiltro();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarDialogoFiltro() {
        String[] opciones = {"Todos", "Audio", "Video", "Streaming"};

        new AlertDialog.Builder(this)
                .setTitle("Selecciona el tipo de recurso")
                .setSingleChoiceItems(opciones, filtroSeleccionado, (dialog, which) -> filtroSeleccionado = which)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    guardarFiltro();
                    aplicarFiltro();
                    Toast.makeText(this, "Filtro aplicado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reproductor != null) {
            reproductor.release();
            reproductor = null;
        }
    }
}
