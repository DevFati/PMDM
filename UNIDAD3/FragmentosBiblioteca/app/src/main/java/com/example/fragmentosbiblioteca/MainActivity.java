package com.example.fragmentosbiblioteca;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final List<Book> listaLibros = new ArrayList<>();
    //El boton cambia su texto y lo que hace segun la pantalla en la que este
    private Button accionBoton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // si la lista de libros esta vacía, se agregan algunos por defecto
        if (listaLibros.isEmpty()) {
            listaLibros.add(new Book("El Quijote", "Miguel de Cervantes", 1605, "Una de las obras más importantes de la literatura."));
            listaLibros.add(new Book("Cien Años de Soledad", "Gabriel García Márquez", 1967, "Crónica de la familia Buendía."));
            listaLibros.add(new Book("1984", "George Orwell", 1949, "Novela distópica sobre un régimen totalitario."));
        }

        // Inicializar el botón
        accionBoton = findViewById(R.id.accion_boton);


            mostrarFragmento(new BookListFragment());

    }

    // Método para acceder a la lista de libros
    public static List<Book> getListaLibros() {
        return listaLibros;
    }

    // Cambiar el texto y funcionalidad del botón cuando se le da clic

    public void configurarBoton(String texto, Runnable accion) {
        //Aqui se cambia el texto del boton
        accionBoton.setText(texto);
        //aqui le indicamos que hacer cuando es clickado
        accionBoton.setOnClickListener(v -> accion.run());
    }

    // Método para mostrar fragmentos
    public void mostrarFragmento(Fragment fragmento) {
        //usamos el administrador de fragmentos para reemplazar la pantalla actual
        //con otra
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, fragmento)
                .addToBackStack(null) //agregamos el cambio a la pila para poder ir para atrás
                .commit();
    }
}
