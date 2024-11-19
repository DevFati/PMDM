package com.example.implicitintentsexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ConfigActivity extends AppCompatActivity {
EditText url;
String dataurl;
Button enviarurl;
Button enviaremail;
EditText correo;
EditText asunto;
EditText mensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_config);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        url=findViewById(R.id.edturl);


        enviarurl=findViewById(R.id.btnguardarURL);
        enviarurl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataurl=url.getText().toString();
                String urlDefinitiva;
                if(validarURL(dataurl)){
                    if (!dataurl.startsWith("http://") && !dataurl.startsWith("https://")) {
                        urlDefinitiva = "https://" + dataurl; // Añadimos esquema si falta
                    } else {
                        urlDefinitiva = dataurl; //significa que esta completa y correcta
                    }

                }else{
                    urlDefinitiva="https://www.google.com/search?q="+dataurl; //lo buscamos en google
                }
                //Devolvemos ressultado a MainActivity
                Intent intent= new Intent();
                intent.putExtra("urlClave",urlDefinitiva); //pasamos lo que esta dentro como un extra
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        //Boton para guardar mail
        enviaremail=findViewById(R.id.btnmail);
        enviaremail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correo=findViewById(R.id.edtmail);
                asunto=findViewById(R.id.edtAsunto);
                mensaje=findViewById(R.id.edtmensaje);

                String correos=correo.getText().toString();
                String asuntos=asunto.getText().toString();
                String mensajes=mensaje.getText().toString();

                //Validamos correo
                if(!Patterns.EMAIL_ADDRESS.matcher(correos).matches()){
                    Toast.makeText(ConfigActivity.this,"Email incorrecto",Toast.LENGTH_SHORT).show();
                return; //No seguimos
                }
                Intent intent= new Intent();
                intent.putExtra("correo",correos);
                intent.putExtra("asunto",asuntos);
                intent.putExtra("mensaje",mensajes);
                setResult(RESULT_OK,intent);
                finish();
            }
        });




    }



    public boolean validarURL(String s) {
        //se restrinje a tld comunes porque por ejemplo sino me dejaria buscar "gato.www" que no existe como url valida
        return s.matches("^(https?:\\/\\/)?([a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+(com|org|net|edu|gov|io|co|us|info)$");
    }




}