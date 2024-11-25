package com.example.fragmentosbiblioteca;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Lista compartida de libros
    private static final List<Book> listaLibros = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cargar libros predefinidos si la lista está vacía
        if (listaLibros.isEmpty()) {
            listaLibros.add(new Book("El Quijote", "Miguel de Cervantes", 1605, "Una de las obras más importantes de la literatura."));
            listaLibros.add(new Book("Cien Años de Soledad", "Gabriel García Márquez", 1967, "Crónica de la familia Buendía."));
            listaLibros.add(new Book("1984", "George Orwell", 1949, "Novela distópica sobre un régimen totalitario."));
        }

        // Mostrar el fragmento de lista al iniciar la aplicación
        if (savedInstanceState == null) {
            BookList fragment = new BookList();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .commit();
        }
    }

    // Método para acceder a la lista de libros desde otras clases
    public static List<Book> getListaLibros() {
        return listaLibros;
    }
}
