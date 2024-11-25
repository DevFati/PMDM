package com.example.fragmentosbiblioteca;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddBookActivity extends AppCompatActivity {

    private EditText titulo, autor, anio, descripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // Inicializar vistas
        titulo = findViewById(R.id.titulo);
        autor = findViewById(R.id.autor);
        anio = findViewById(R.id.anio);
        descripcion = findViewById(R.id.descripcion);
        Button guardar = findViewById(R.id.guardar);

        // Acción del botón guardar
        guardar.setOnClickListener(v -> {
            //obtenemos los valores que el usuario ha introducido
            String tituloLibro = titulo.getText().toString().trim();
            String autorLibro = autor.getText().toString().trim();
            String anioStr = anio.getText().toString().trim();
            String descripcionLibro = descripcion.getText().toString().trim();
            //evitamos que introduzca campos vacíos
            if (tituloLibro.isEmpty() || autorLibro.isEmpty() || anioStr.isEmpty() || descripcionLibro.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
                return; //Si deja un campo vacío, no lo dejamos seguir.
            }
                int anioLibro = Integer.parseInt(anioStr);
                if (anioLibro < 0 || anioLibro > 2024) {
                    Toast.makeText(this, "Por favor, ingresa un año válido.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Agregar el libro a la lista
                MainActivity.getListaLibros().add(new Book(tituloLibro, autorLibro, anioLibro, descripcionLibro));
                Toast.makeText(this, "Libro guardado: " + tituloLibro, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();

        });
    }
}

