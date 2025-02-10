package com.example.fatimamortahilpreferencias;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_details);

        TextView txtDetalles = findViewById(R.id.txtDetalles);

        // Obtener datos desde el intent
        String nombre = getIntent().getStringExtra("nombre");
        String empresa = getIntent().getStringExtra("empresa");
        String email = getIntent().getStringExtra("email");
        int edad = getIntent().getIntExtra("edad", 0);
        float sueldo = getIntent().getFloatExtra("sueldo", 0.0f);

        // Mostrar los datos en un párrafo
        String detalles = "Datos del Usuario:\n\n" +
                "Nombre: " + nombre + "\n" +
                "Empresa: " + empresa + "\n" +
                "Email: " + email + "\n" +
                "Edad: " + edad + "\n" +
                "Sueldo: " + sueldo;
        txtDetalles.setText(detalles);
    }
}
