package com.example.fragmentosbiblioteca;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddBookActivity extends AppCompatActivity {

    private EditText titulo, autor, anio, descripcion;
    private Button guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // Inicializar vistas
        titulo = findViewById(R.id.titulo);
        autor = findViewById(R.id.autor);
        anio = findViewById(R.id.anio);
        descripcion = findViewById(R.id.descripcion);
        guardar = findViewById(R.id.guardar);

        // Acción del botón guardar
        guardar.setOnClickListener(v -> {
            String tituloLibro = titulo.getText().toString().trim();
            String autorLibro = autor.getText().toString().trim();
            String anioStr = anio.getText().toString().trim();
            String descripcionLibro = descripcion.getText().toString().trim();

            if (tituloLibro.isEmpty() || autorLibro.isEmpty() || anioStr.isEmpty() || descripcionLibro.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int anioLibro = Integer.parseInt(anioStr);
                if (anioLibro < 0 || anioLibro > 2024) {
                    Toast.makeText(this, "Por favor, ingresa un año válido.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Aquí puedes agregar lógica para enviar el libro a la lista principal
                Toast.makeText(this, "Libro guardado: " + tituloLibro, Toast.LENGTH_SHORT).show();
                finish();

            } catch (NumberFormatException e) {
                Toast.makeText(this, "El año debe ser un número válido.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
