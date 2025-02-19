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
    private RecyclerView listaRecursos; //RecyclerView para que se vea la lista de medios
    private MediaAdapter adaptadorMedios; //Este es el adaptador del Recycleriew
    private List<MediaItem> listaMedios = new ArrayList<>(); //lista completa de medios cargados
    private List<MediaItem> listaFiltrada = new ArrayList<>(); //lista de medios despues de aplicar los filtros
    private int filtroSeleccionado = 0; // 0 = Todos, 1 = Audio, 2 = Video, 3 = Streaming

    private MediaPlayer reproductor; //Mediaplayer para que funcionen los audios.
    private MediaController controlador; //Controlador de reprodución para el audio
    private ViewGroup contenedorReproductor; //Contenedor donde se ancla el controlador de audio

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaRecursos = findViewById(R.id.listaRecursos);
        listaRecursos.setLayoutManager(new LinearLayoutManager(this));
        //Inicializa el adaptador y se lo asigna al RecyclerView
        adaptadorMedios = new MediaAdapter(listaFiltrada, this, this::iniciarReproduccionAudio);
        listaRecursos.setAdapter(adaptadorMedios);

        contenedorReproductor = findViewById(R.id.contenedorReproductor); //Contenedor del reproductor
        controlador = new MediaController(this); //Crea un nuevo MediaController para la reproduccion de audio

        cargarMediosDesdeJson(); //Carga la lista de medios desde un archivo JSON
        cargarFiltro(); //Carga la configuración de filtros guardada en preferencias
    }

    //Método que carga la lista de medios desde un archivo JSON en la carpeta "assets"
    private void cargarMediosDesdeJson() {
        try {
            InputStream entrada = getAssets().open("recursosList.json"); //Abre el archivo JSON
            int tamaño = entrada.available(); //Obtiene el tamaño del archivo
            byte[] buffer = new byte[tamaño];// crea un buffer para almacenar los datos
            entrada.read(buffer); //lee los datos del archivo JSON en el buffer
            entrada.close(); //aqui cerramos el JSON
            String json = new String(buffer, "UTF-8"); //Convertimos los datos a String

            JSONObject jsonObject = new JSONObject(json); //Crea un objeto JSON desde el string
            JSONArray jsonArray = jsonObject.getJSONArray("recursos_list"); //Extrae el arreglo de medios
            listaMedios.clear(); //Limpia la lista antes de cargar los nuevos datos
            //Iteramos sobre cada elemento del JSON y lo agregamos a la lista de medios
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                listaMedios.add(new MediaItem(
                        obj.getString("nombre"),
                        obj.getString("descripcion"),
                        obj.getInt("tipo"),
                        obj.getString("URI"),
                        obj.getString("imagen")));
            }
            aplicarFiltro(); //Aplicamos el filtro actual después de cargar los medios
        } catch (IOException | JSONException e) {
            if (e instanceof IOException) {
                Toast.makeText(this, "No se encontró el archivo JSON", Toast.LENGTH_SHORT).show();

            } else if (e instanceof JSONException) {
                Toast.makeText(this, "JSON vacío", Toast.LENGTH_SHORT).show();

            }
        }
    }

    //Método para iniciar la reproducción de audio
    public void iniciarReproduccionAudio(String uri) {
        detenerAudioSiEsNecesario(); // nos aseguramos de que no haya otro audio sonando
        int idRecurso = getResources().getIdentifier(uri, "raw", getPackageName()); //Obtiene el ID del recurso
        reproductor = MediaPlayer.create(this, idRecurso); //Crea el reproductor con el recurso de audio
        if (reproductor != null) {
            reproductor.start();//Se inicia la reproducción del audio
        }


        //Configuramos el controlador de audio
        controlador.setAnchorView(contenedorReproductor);
        controlador.setMediaPlayer(new MediaController.MediaPlayerControl() {
            @Override
            public void start() {
                if (reproductor != null) reproductor.start();
            }

            @Override
            public void pause() {
                if (reproductor != null) reproductor.pause();
            }

            @Override
            public int getDuration() {
                return reproductor != null ? reproductor.getDuration() : 0;
            }

            @Override
            public int getCurrentPosition() {
                return reproductor != null ? reproductor.getCurrentPosition() : 0;
            }

            @Override
            public void seekTo(int pos) {
                if (reproductor != null) reproductor.seekTo(pos);
            }

            @Override
            public boolean isPlaying() {
                return reproductor != null && reproductor.isPlaying();
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
                return reproductor != null ? reproductor.getAudioSessionId() : 0;
            }
        });

        controlador.show(0); //Muestra el controlador permanentemente
    }


    public void detenerAudioSiEsNecesario() {
        if (reproductor != null) {
            if (reproductor.isPlaying()) {
                reproductor.pause();
            }

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            controlador.show(0);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona el tipo de recurso")
                .setSingleChoiceItems(opciones, filtroSeleccionado, (dialog, which) -> filtroSeleccionado = which)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    guardarFiltro();
                    aplicarFiltro();
                    Toast.makeText(this, "Filtro aplicado", Toast.LENGTH_SHORT).show();
                })
                .setCancelable(false); // Evita que se pueda cerrar pulsando fuera

        AlertDialog dialogo = builder.create();
        dialogo.show();
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
