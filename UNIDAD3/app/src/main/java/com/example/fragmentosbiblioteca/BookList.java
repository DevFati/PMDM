package com.example.fragmentosbiblioteca;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class BookListFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookAdapter adaptador;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        // Obtener la lista compartida de libros
        List<Book> listaLibros = MainActivity.getListaLibros();

        // Configurar RecyclerView
        recyclerView = view.findViewById(R.id.lista_libros);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Configurar adaptador
        adaptador = new BookAdapter(listaLibros, this::abrirDetallesLibro);
        recyclerView.setAdapter(adaptador);

        // Configurar botón flotante para agregar libros
        FloatingActionButton botonAgregar = view.findViewById(R.id.boton_agregar_libro);
        botonAgregar.setOnClickListener(v -> {
            // Abrir la actividad para agregar libros
            startActivityForResult(new Intent(getContext(), AddBookActivity.class), 1);
        });

        return view;
    }

    // Refrescar la lista al regresar de agregar un libro
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            // Notificar cambios al adaptador
            adaptador.notifyDataSetChanged();
        }
    }

    // Abrir los detalles del libro seleccionado
    private void abrirDetallesLibro(Book libro) {
        BookDetailFragment detalleFragmento = new BookDetailFragment();

        // Pasar el libro seleccionado al fragmento
        Bundle argumentos = new Bundle();
        argumentos.putSerializable("libro", libro);
        detalleFragmento.setArguments(argumentos);

        // Reemplazar el fragmento actual por el de detalles
        getParentFragmentManager().beginTransaction()
                .replace(R.id.main_container, detalleFragmento)
                .addToBackStack(null)
                .commit();
    }
}

