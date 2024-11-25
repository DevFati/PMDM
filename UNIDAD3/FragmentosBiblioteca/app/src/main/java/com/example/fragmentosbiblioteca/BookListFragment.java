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

import java.util.List;
//Fragmento que muestra la lista de libros
public class BookListFragment extends Fragment {
//Aqui se guarda el recyclerview
    private RecyclerView recyclerView;
    //El adaptador le dice al recyclerview como mostrar los libros
    private BookAdapter adaptador;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        // Obtener la lista de libros que estan guardados en main
        //contiene todos los libros agregados
        List<Book> listaLibros = MainActivity.getListaLibros();

        // Configurar RecyclerView
        recyclerView = view.findViewById(R.id.lista_libros);
        //Le estamos indicando que los muestre uno debajo de otro
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Aqui le indicamos al recyclerview que libros queremos que aparezan y el comportamiento
        //al pulsar en cada uno de ellos.
        adaptador = new BookAdapter(listaLibros, this::abrirDetallesLibro);
        //Conectamos el adaptador al recyclerView
        recyclerView.setAdapter(adaptador);

        // Configurar el botón que esta en la mainActivity

        //Cuando pulsamos el boton, abrimos la pantalla para agreegar un nuevo libro
        ((MainActivity) requireActivity()).configurarBoton("Agregar libro", () -> {
            //Usamos un intent para ir a la actividad addbookactivity, donde podemos agregar libros.
            startActivity(new Intent(getContext(), AddBookActivity.class));
        });

        return view;
    }

    // llamamos a este metodo cuando se toque un libro de la lista,
    //mostraremos los detalles del libro que el usuario a selccionado.
    private void abrirDetallesLibro(Book libro) {
        //Se crea un nuevo fragmento para mostrar los detalles del libro.
        BookDetailFragment detalleFragmento = new BookDetailFragment();

        // Pasar el libro seleccionado al fragmento
        Bundle argumentos = new Bundle();
        argumentos.putSerializable("libro", libro);
        detalleFragmento.setArguments(argumentos);

        // Mostrar el fragmento de detalles en lugar de la lista
        ((MainActivity) requireActivity()).mostrarFragmento(detalleFragmento);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Notificar al adaptador que la lista cambió para que se actualice la vista
        if (adaptador != null) {
            adaptador.notifyDataSetChanged();
        }
    }

}


