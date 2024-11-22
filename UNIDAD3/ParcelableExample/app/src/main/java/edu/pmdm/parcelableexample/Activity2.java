package edu.pmdm.parcelableexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Activity2 extends AppCompatActivity {
    //Tuve que añadir esta activity al AndroidManifest
    //para que el sistema pudiese reconocerla y gestionarla correctamente
private TextView j;
private Button volver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);
//En este textview podremos observar todos los datos
         j=findViewById(R.id.edtxtDatos);
        volver=findViewById(R.id.btnVolver);

        // Recuperar los datos del intent
        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre");
        String edad = intent.getStringExtra("edad");
        String curso = intent.getStringExtra("curso");
        String telefono = intent.getStringExtra("telefono");
        ArrayList<Asignatura> listaAsignaturas = intent.getParcelableArrayListExtra("listaAsignaturas");

        // mostrar la salida en formato json
        //Usamos Stringbuilder para concatenar de manera mas sencilla y eficiente

        StringBuilder detalles = new StringBuilder();
        detalles.append("{\n");
        detalles.append("    \"nombre\": \"").append(nombre).append("\",\n");
        detalles.append("    \"edad\": ").append(edad).append(",\n");
        detalles.append("    \"curso\": \"").append(curso).append("\",\n");
        detalles.append("    \"telefono\": \"").append(telefono).append("\",\n");
        detalles.append("    \"asignaturas\": [\n");
        //solo listamos cuando hay datos dentro
        if (listaAsignaturas != null && !listaAsignaturas.isEmpty()) {
            for (int i = 0; i < listaAsignaturas.size(); i++) {
                Asignatura asignatura = listaAsignaturas.get(i);
                detalles.append("        {\n");
                detalles.append("            \"nombre\": \"").append(asignatura.getNombre()).append("\",\n");
                detalles.append("            \"nota\": ").append(asignatura.getNota()).append("\n");
                detalles.append("        }");
                if (i < listaAsignaturas.size() - 1) {
                    detalles.append(",");
                }
                detalles.append("\n");
            }
        }

        detalles.append("    ]\n");
        detalles.append("}");

        j.setText(detalles.toString());

        volver.setOnClickListener(v -> finish());

    }
}
