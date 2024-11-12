package edu.pmdm.actividad3pantallas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class MainActivity extends AppCompatActivity {
    //Declarar variables
    private EditText nombre, edad, ciudad;
    private RadioGroup preferencias;
    private Button continuar;
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

        //Inicializar las variables
        nombre=findViewById(R.id.edtNom);
        edad=findViewById(R.id.edtedad);
        ciudad=findViewById(R.id.edtciudad);
        preferencias=findViewById(R.id.radioGroup);
        continuar=findViewById(R.id.btnContinuar);
        //Desabilitar el botón de continuar.
        continuar.setEnabled(false);

        //Pongo el mensaje aqui porque si el usuario no introduce
        //una edad valida entre 3 y 100 y nombre y ciudad validos el boton no
        //se habilitara.
        Toast.makeText(this,"Para continuar debes de rellenar todos los campos de manera correcta",Toast.LENGTH_SHORT).show();

        //TextWatcher es para cada campo (nombre,edad, ciudad)
        //para validar la información si es válida en tiempo real.

        nombre.addTextChangedListener(new Modificado());
        edad.addTextChangedListener(new Modificado());
        ciudad.addTextChangedListener(new Modificado());
        //RadioGroup no hace falta validarlo porque siempre sale
        //una seleccionada por defecto

        //Cuando los campos estan bien validados, se habilita el boton
        //y el usuario puede dar click
        continuar.setOnClickListener(
                v -> {

                        //Obtenemos el id del radiobutton seleccionado en el radiogroup
                        int seleccionado=preferencias.getCheckedRadioButtonId();
                        //Obtiene el texto del radiobutton seleccionado
                        RadioButton pref=findViewById(seleccionado);
                        String preferencia=pref.getText().toString();

                        //Creamos un intent para pasar los datos a la actividad pantallaRevisar
                        Intent in=new Intent(this, PantallaRevisar.class);
                        in.putExtra("NOMBRE",nombre.getText().toString());
                        in.putExtra("EDAD",edad.getText().toString());
                        in.putExtra("CIUDAD",ciudad.getText().toString());
                        in.putExtra("PREFERENCIA",preferencia);
                        //Inicializamos la actividad PantallaRevisar
                        startActivity(in);

                }
        );

    }

    //Clase interna para controlar todos los cambios en los campos de texto
    // y comprobar si son validos o no
    private class Modificado implements TextWatcher {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            // Después de que el texto ha cambiado, se validan los campos

            if(validar()){
                //Si todos los campos son válidos, habilita el botón "Continuar".
                continuar.setEnabled(true);
            }else{
                //Si no todos los campos son válidos, desabilita el botón "Continuar".
                continuar.setEnabled(false);
            }

        }
    }


    // Método para validar si una palabra contiene letras, no se admiten caracteres especiales.
    private boolean palabraCorrecta(String p){
        if (p.matches("[a-zA-Z]+")) {

            return true;
        } else {
           return false;
        }
    }

    //Metodo para validar que los campos no esten vacios y contengan los datos correctos.
    private boolean validar(){
        if(!nombre.getText().toString().isEmpty() && !edad.getText().toString().isEmpty()
                && !ciudad.getText().toString().isEmpty() && palabraCorrecta(nombre.getText().toString()) &&
                palabraCorrecta(ciudad.getText().toString()) ){
            int ed=Integer.parseInt(edad.getText().toString());
            if(ed>=3 && ed<=100){
                return true;
            }else{
                return false;
            }


        }else{

            return false;
        }
    }
    @Override
    //Se llama este metodo cuando la actividad se reanuda.
    protected void onResume(){
        //cada vez que esta actividad este visible se limpia
        super.onResume();

        nombre.setText("");
        edad.setText("");
        ciudad.setText("");
        preferencias.check(R.id.rbgato); //se selecciona por defecto gato
    }
}