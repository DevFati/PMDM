package edu.pmdm.actividad3pantallas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PantallaRevisar extends AppCompatActivity {
    //Declaracion de las vistas de la interfaz.

    private TextView nombre, edad, ciudad, preferencia;
    private Button editar,volver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_revisar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Inicializar las vistas.
        nombre=findViewById(R.id.txtNombre);
        edad=findViewById(R.id.txtEdad);
        ciudad=findViewById(R.id.txtCiudad);
        preferencia=findViewById(R.id.txtPref);
        editar=findViewById(R.id.btnMod);
        volver=findViewById(R.id.btnVolver);

        //Recupera y establece los datos enviados desde la actividad anterior en los TextViews.
        nombre.setText(getIntent().getStringExtra("NOMBRE"));
        edad.setText(getIntent().getStringExtra("EDAD"));
        ciudad.setText(getIntent().getStringExtra("CIUDAD"));
        preferencia.setText(getIntent().getStringExtra("PREFERENCIA"));

        //Listener para el boton de editar.
        editar.setOnClickListener(v -> {
            //Crea un intent para iniciar la actividad pantallaEditar y pasa los datos actuales.
            Intent in=new Intent(this, PantallaEditar.class);
            in.putExtra("NOMBRE",nombre.getText().toString());
            in.putExtra("EDAD",edad.getText().toString());
            in.putExtra("CIUDAD",ciudad.getText().toString());
            in.putExtra("PREFERENCIA",preferencia.getText().toString());

            startActivity(in); //Inicia la actividad pantallaEditar

        });

        //Listener para el botón "Volver"
        volver.setOnClickListener(v -> {
            //Muesra el mensaje de confirmación que los datos fueron guardados correctamente.
            Toast.makeText(this,"Datos guardados correctamente",Toast.LENGTH_SHORT).show();
            //Crea un Intent para regresar a la actividad principal
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent); //Inicia MainActivity

        });


    }
}