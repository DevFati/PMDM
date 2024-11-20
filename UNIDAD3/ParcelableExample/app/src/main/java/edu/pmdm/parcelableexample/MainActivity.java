package edu.pmdm.parcelableexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
private Button añadirAlumno,añadirAsignatura,verDetalles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        añadirAlumno=findViewById(R.id.btnAñadirEstudiante);
        añadirAsignatura=findViewById(R.id.btnAsignaturas);
        verDetalles=findViewById(R.id.btnDetalles);


        //Configuramos para que se añada un alumno al darle clic al boton

        añadirAlumno.setOnClickListener(v -> {

        });



    }
}