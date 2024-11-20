package com.example.implicitintentsexample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
Button botonfoto;
String url="https://www.google.com"; //Si el usuario no pone nada, esta sera la url por defecto
Button config;
Button mail;
String email,asunto,mensaje;
//para recibir los datos de ConfigActivity
private ActivityResultLauncher<Intent> configLauncher;
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
     //Configuramos el activityresultlauncher
    configLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
            if(o.getResultCode()==RESULT_OK){
                Intent data=o.getData();
                if(data!=null){
                     String durl=data.getStringExtra("urlClave"); //leemos URL
                     email=data.getStringExtra("correo");
                     asunto=data.getStringExtra("asunto");
                     mensaje=data.getStringExtra("mensaje");

                    if(durl!=null){
                        url=durl; //Si no es nula pues
                        //cogemos el valor introducido por el usuario
                    }
                }

            }
                }
            }
    );
        // Botón para abrir una página web
        Button btnAbrirWeb = findViewById(R.id.btnAbrirWeb);
        btnAbrirWeb.setOnClickListener(v -> {
            // Crear un intent implícito para abrir una página web
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        // Botón para enviar mail
        mail  = findViewById(R.id.btnenviarmail);
        mail.setOnClickListener(v -> {
            if(email==null || asunto==null || mensaje==null || email.isEmpty() || asunto.isEmpty() || mensaje.isEmpty()){

                Toast.makeText(MainActivity.this, "Todos los elementos del mail tienen que estar rellenados", Toast.LENGTH_SHORT).show();
                return;
            }

            //construimos URI manualmente (se ha intentado automaticamente pero no funciona)
            String u="mailto:"+email+"?subject="+Uri.encode(asunto)+"&body="+Uri.encode(mensaje);
            Uri ur=Uri.parse(u);


            // Crear un intent implícito para abrir un mail
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(ur); // se usa mailto para que solo nos muestre apps de mensajeria

            startActivity(Intent.createChooser(intent,"Enviar email..."));
        });


        // Botón para realizar una llamada
        Button btnLlamar = findViewById(R.id.btnLlamar);
        btnLlamar.setOnClickListener(v -> {
            // Crear un intent implícito para realizar una llamada
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:123456789"));
            startActivity(intent);
        });

        // Botón para realizar una foto
        botonfoto = findViewById(R.id.btnfoto);
        botonfoto.setOnClickListener(v -> {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(intent);
        });

        // Botón para entrar a configurar
        config = findViewById(R.id.btnconfig);
        config.setOnClickListener(v -> {
            Intent i=new Intent(this,ConfigActivity.class);
            configLauncher.launch(i);
        });


    }
}