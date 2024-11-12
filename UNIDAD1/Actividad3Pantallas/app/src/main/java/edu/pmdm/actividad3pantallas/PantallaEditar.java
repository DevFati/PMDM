package edu.pmdm.actividad3pantallas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PantallaEditar extends AppCompatActivity {
    //Declaración de los elementos de la interfaz.
    private EditText nombre, edad, ciudad;
    private RadioGroup preferencias;
    private Button guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_editar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Inicializa las vistas.
        nombre=findViewById(R.id.edtNombre);
        edad=findViewById(R.id.edtEdad);
        ciudad=findViewById(R.id.edtCiudad);
        preferencias=findViewById(R.id.radioGroup);
        guardar=findViewById(R.id.btnGuardar);
        //recupera y establece los datos enviados desde la pantallaEditar
        nombre.setText(getIntent().getStringExtra("NOMBRE"));
        edad.setText(getIntent().getStringExtra("EDAD"));
        ciudad.setText(getIntent().getStringExtra("CIUDAD"));
        String pref = getIntent().getStringExtra("PREFERENCIA");

        //Marca el radiobutton correcto
        if (pref.equals("Gato")) {
            ((RadioButton) preferencias.getChildAt(0)).setChecked(true);
        } else {
            ((RadioButton) preferencias.getChildAt(1)).setChecked(true);
        }


        guardar.setOnClickListener(v -> {
            //validar que los campos no esten vacios y sean validos
            if(!nombre.getText().toString().isEmpty() && !edad.getText().toString().isEmpty()
                    && !ciudad.getText().toString().isEmpty() && palabraCorrecta(nombre.getText().toString()) &&
                    palabraCorrecta(ciudad.getText().toString()) ){
                int ed=Integer.parseInt(edad.getText().toString());
                //verifica que la edad este en el rango de 3 a 100 años
                if(ed>=3 && ed<=100) {
                //Obtiene la preferencia selecionado

                    int seleccionado = preferencias.getCheckedRadioButtonId();
                    RadioButton r = findViewById(seleccionado);
                    String prefer = r.getText().toString();

                    //Crea un intent para pasar los datos de vuelta a la actividad PantallaRevisar
                    Intent in = new Intent(this, PantallaRevisar.class);
                    in.putExtra("NOMBRE", nombre.getText().toString());
                    in.putExtra("EDAD", edad.getText().toString());
                    in.putExtra("CIUDAD", ciudad.getText().toString());
                    in.putExtra("PREFERENCIA", prefer);
                    //Inicia la actividad pantallaRevisar
                    startActivity(in);

                }else{
                    //Muestra un mensaje de error si algun campo esta vacio o es invalido
                    Toast.makeText(this,"Para continuar debes de rellenar todos los campos de manera correcta",Toast.LENGTH_SHORT).show();

                }

            }else{
                Toast.makeText(this,"Para continuar debes de rellenar todos los campos de manera correcta",Toast.LENGTH_SHORT).show();
            }


        });

    }

    private boolean palabraCorrecta(String p){
        if (p.matches("[a-zA-Z]+")) {
            return true;
        } else {
            return false;
        }
    }
}