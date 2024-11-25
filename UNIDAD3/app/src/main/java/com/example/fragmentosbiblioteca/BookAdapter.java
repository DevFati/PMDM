package com.example.fragmentosbiblioteca;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> libros;

    public BookAdapter(List<Book> libros) {
        this.libros = libros;
    }



    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book libro = libros.get(position);
        holder.titulo.setText(libro.getTitulo());
    }

    @Override
    public int getItemCount() {
        return libros.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titulo_item);
        }
    }
}
