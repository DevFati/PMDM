package com.example.fragmentosbiblioteca;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Fragmento para mostrar los detalles del libro
public class BookDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_book, container, false);

        // Obtener el libro pasado como argumento
        Bundle argumentos = getArguments();
        if (argumentos == null || !argumentos.containsKey("libro")) {
            throw new IllegalArgumentException("No se proporcionó ningún libro.");
        }

        Book libro = (Book) argumentos.getSerializable("libro");

        // Configurar vistas para mostrar los detalles
        TextView titulo = vista.findViewById(R.id.detalle_titulo);
        TextView autor = vista.findViewById(R.id.detalle_autor);
        TextView anio = vista.findViewById(R.id.detalle_anio);
        TextView descripcion = vista.findViewById(R.id.detalle_descripcion);

        titulo.setText(libro.getTitulo());
        autor.setText(libro.getAutor());
        anio.setText(String.valueOf(libro.getAnio()));
        descripcion.setText(libro.getDescripcion());

        // Configurar botón "Volver"
        vista.findViewById(R.id.volver).setOnClickListener(v -> {
            // Regresar al fragmento de lista
            getParentFragmentManager().popBackStack();
        });

        return vista;
    }
}

