package com.example.fatimamortahiltarea6;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.VistaMedio> {
    private List<MediaItem> listaMedios; //Listaque almacena los elementos de multimedia
    private Context contexto; //Contexto de la aplicación
    private OnAudioPlayListener listener; //Interfaz para manejar la reproducción del audio

    //Interfaz para notificar la reproducción de audio
    public interface OnAudioPlayListener {
        void iniciarReproduccionAudio(String uri);
    }

    //Constructor de la clase
    public MediaAdapter(List<MediaItem> listaMedios, Context contexto, OnAudioPlayListener listener) {
        this.listaMedios = listaMedios;
        this.contexto = contexto;
        this.listener = listener;

    }

    @NonNull
    @Override
    public VistaMedio onCreateViewHolder(@NonNull ViewGroup padre, int tipoVista) {
        View vista = LayoutInflater.from(contexto).inflate(R.layout.media_item, padre, false);
        return new VistaMedio(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull VistaMedio vistaMedio, int posicion) {
        //Obtiene el elemento de la lista en la posicion actual
        MediaItem medio = listaMedios.get(posicion);
        //Asigna el nombre y la descripcion al textview
        vistaMedio.textoNombre.setText(medio.obtenerNombre());
        vistaMedio.textoDescripcion.setText(medio.obtenerDescripcion());
        //Segun el tipo de medio, asignamos un icono u otro
        int iconoRecurso;
        switch (medio.obtenerTipo()) {
            case 1:
                iconoRecurso = R.drawable.video;
                break;
            case 2:
                iconoRecurso = R.drawable.streaming;
                break;
            default:
                iconoRecurso = R.drawable.audio;
                break;
        }
        vistaMedio.iconoTipo.setImageResource(iconoRecurso);
// Cargar la imagen desde assets/images/
        try {
            InputStream entrada = contexto.getAssets().open("images/" + medio.obtenerImagen());
            Drawable imagen = Drawable.createFromStream(entrada, null);
            vistaMedio.imagenPortada.setImageDrawable(imagen);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //boton para el play de cada medio
        vistaMedio.botonReproducir.setOnClickListener(v -> {
            ((MainActivity) contexto).detenerAudioSiEsNecesario();
            Intent intent;
            if (medio.obtenerTipo() == 0) { //si es un audio
                listener.iniciarReproduccionAudio(medio.obtenerUri());
            } else { //si es video o streaming
                intent = new Intent(contexto, VideoPlayerActivity.class);
                intent.putExtra("URI", medio.obtenerUri());
                contexto.startActivity(intent); //iniciamos la actividad para empezar el video
            }

        });
    }

    @Override
    public int getItemCount() {
        return listaMedios.size();
    } //Devuelve los elementos de la lista


    public static class VistaMedio extends RecyclerView.ViewHolder {
        TextView textoNombre, textoDescripcion;
        ImageView iconoTipo, imagenPortada;
        Button botonReproducir;

        public VistaMedio(View itemVista) {
            super(itemVista);
            textoNombre = itemVista.findViewById(R.id.textoNombre);
            textoDescripcion = itemVista.findViewById(R.id.textoDescripcion);
            iconoTipo = itemVista.findViewById(R.id.iconoTipo);
            imagenPortada = itemVista.findViewById(R.id.imagenPortada);
            botonReproducir = itemVista.findViewById(R.id.botonReproducir);
        }
    }
}
