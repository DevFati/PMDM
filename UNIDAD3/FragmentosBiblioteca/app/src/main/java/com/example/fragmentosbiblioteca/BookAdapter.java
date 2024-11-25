package com.example.fragmentosbiblioteca;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Adaptador para la lista de libros en el recyclerview
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private OnBookClickListener listener;
    // Lista de libros
    private List<Book> listaLibros;

    // Interfaz para cuando se de clic en los libros.
    public interface OnBookClickListener {
        void onBookClick(Book libro);
    }



    // Constructor del adaptador
    public BookAdapter(List<Book> listaLibros, OnBookClickListener listener) {
        this.listaLibros = listaLibros;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout para cada elemento del recyclerview
        //con item_book se refiere a cada libro de la lista
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view); //Y devuelve una instancia del viewholder con la vista inflada
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        // Obtener el libro actual de la posicion seleccionada por el usuario
        Book libro = listaLibros.get(position);

        // Configurar el titulo del libro en el textview del viewholder
        holder.titulo.setText(libro.getTitulo());

        // cuando se hace clic, se llama al metodo onBookClick con el libro en cuestión
        holder.itemView.setOnClickListener(v -> listener.onBookClick(libro));
    }

    @Override
    public int getItemCount() {
        return listaLibros.size();
    }

    // Clase interna para el ViewHolder
    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            // Vincula el textview del layout item_book al atributo titulo
            titulo = itemView.findViewById(R.id.titulo_item);
        }
    }
}

