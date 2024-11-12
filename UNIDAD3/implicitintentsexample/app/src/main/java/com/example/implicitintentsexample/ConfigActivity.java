package com.example.implicitintentsexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

                Intent intent= new Intent(ConfigActivity.this,MainActivity.class);
                intent.putExtra("urlClave",dataurl); //pasamos lo que esta dentro como un extra
                setResult(RESULT_OK,intent);
                finish();
            }
        });


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

                Intent intent= new Intent(ConfigActivity.this,MainActivity.class);
               // intent.putExtra("correoD",correos,"asuntoD",asuntos,"mensajeD",mensajes); //pasamos lo que esta dentro como un extra
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }
}