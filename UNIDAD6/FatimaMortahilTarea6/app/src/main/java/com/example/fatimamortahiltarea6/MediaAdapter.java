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
    private List<MediaItem> listaMedios;
    private Context contexto;
    private OnAudioPlayListener listener;

    public interface OnAudioPlayListener {
        void iniciarReproduccionAudio(String uri);
    }

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
        MediaItem medio = listaMedios.get(posicion);
        vistaMedio.textoNombre.setText(medio.obtenerNombre());
        vistaMedio.textoDescripcion.setText(medio.obtenerDescripcion());

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

        vistaMedio.botonReproducir.setOnClickListener(v -> {
            ((MainActivity) contexto).detenerAudioSiEsNecesario();
            Intent intent;
            if (medio.obtenerTipo() == 0) {
                listener.iniciarReproduccionAudio(medio.obtenerUri());
            } else {
                intent = new Intent(contexto, VideoPlayerActivity.class);
                intent.putExtra("URI", medio.obtenerUri());
                contexto.startActivity(intent);
            }

        });
    }

    @Override
    public int getItemCount() {
        return listaMedios.size();
    }

    public static class VistaMedio extends RecyclerView.ViewHolder {
        TextView textoNombre, textoDescripcion;
        ImageView iconoTipo,imagenPortada;
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
