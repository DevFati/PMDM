package edu.pmdm.parcelableexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
private EditText etNombre, etEdad, etCurso, edTelefono;
private Button btnAsig,btnEnviar;
private ArrayList<Asignatura> listaAsignaturas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
    //Inicializacion de campos y botones
        etNombre=findViewById(R.id.edtxtNombre);
        etNombre.setEnabled(true);
        etEdad=findViewById(R.id.edtxtEdad);
        etCurso=findViewById(R.id.edtxtCurso);
        edTelefono=findViewById(R.id.edtxtTel);
        listaAsignaturas = new ArrayList<>();
        btnAsig=findViewById(R.id.btnNuevaAsignatura);
        btnEnviar=findViewById(R.id.btnEnviar);


        btnEnviar.setOnClickListener(v -> {
            enviarDatos();
        });

        btnAsig.setOnClickListener(v -> {
            agregarAsig();
        });

    }

    private void enviarDatos() {
        String nombre = etNombre.getText().toString().trim();
        String edad = etEdad.getText().toString().trim();
        String curso = etCurso.getText().toString().trim();
        String telefono = edTelefono.getText().toString().trim();

        if (nombre.isEmpty() || edad.isEmpty() || curso.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Todos los campos deben estar rellenos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(Integer.parseInt(edad)>99 || Integer.parseInt(edad)<4){
            Toast.makeText(this, "Edad no aceptada, adjunta certificado de nacimiento al administrador para comprobar su veracidad.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!telefono.matches("^[+]?[0-9]{9,10}$")){
            Toast.makeText(this, "Telefono incorrecto", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$") || nombre.length()<3){
            Toast.makeText(this, "Nombre incorrecto", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear intent y pasar los datos
        Intent intent = new Intent(this, Activity2.class);
        intent.putExtra("nombre", nombre);
        intent.putExtra("edad", edad);
        intent.putExtra("curso", curso);
        intent.putExtra("telefono", telefono);
        intent.putParcelableArrayListExtra("listaAsignaturas", listaAsignaturas);

        startActivity(intent);
    }

    private void agregarAsig() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar asignatura");

        View v = LayoutInflater.from(this).inflate(R.layout.nueva_asignatura, null);
         EditText nombreAsig = v.findViewById(R.id.edtxtNomAsign);
         EditText notasAsign = v.findViewById(R.id.edtxtNotaAsign);

        builder.setView(v);
        builder.setPositiveButton("Agregar", (dialog, which) -> {
            String nombre = nombreAsig.getText().toString();
            String notaA = notasAsign.getText().toString();

            if (nombre.isEmpty()|| notaA.isEmpty()) {
                Toast.makeText(MainActivity.this, "Todos los campos deben estar rellenados. ", Toast.LENGTH_SHORT).show();
                return;
            }

            if(Double.parseDouble(notaA)<0 || Double.parseDouble(notaA)>10 || !notaA.matches("^(\\d{1,2})(\\.\\d{1,2})?$")){
                Toast.makeText(MainActivity.this, "Nota inválida ", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$") || nombre.length()<3){
                Toast.makeText(this, "Nombre asignatura incorrecto", Toast.LENGTH_SHORT).show();
                return;
            }

            //Vemos si la asignatura ya existe
            boolean existe = false;
            for (Asignatura a : listaAsignaturas) {
                if (a.getNombre().equalsIgnoreCase(nombre)) {
                    existe = true;
                    break;
                }
            }

            if (existe) {
                Toast.makeText(MainActivity.this, "No puedes meter asignaturas duplicadas.", Toast.LENGTH_SHORT).show();
            } else {
                Asignatura asignatura = new Asignatura(nombre, Double.parseDouble(notaA));
                listaAsignaturas.add(asignatura);
                Toast.makeText(this, "Asignatura añadida a la lista.", Toast.LENGTH_SHORT).show();

            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }


}