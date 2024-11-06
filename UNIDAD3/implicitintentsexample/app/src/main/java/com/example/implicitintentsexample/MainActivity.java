package com.example.implicitintentsexample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
String url;
Button config;
Button mail;
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
        url="google.com";
    configLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
            if(o.getResultCode()==RESULT_OK){
                Intent data=o.getData();
                if(data!=null){
                     url=data.getStringExtra("urlClave");

                }

            }
                }
            }
    );
        // Botón para abrir una página web
        Button btnAbrirWeb = findViewById(R.id.btnAbrirWeb);
        btnAbrirWeb.setOnClickListener(v -> {
            // Crear un intent implícito para abrir una página web
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://"+url));
            startActivity(intent);
        });

        // Botón para enviar mail
        mail  = findViewById(R.id.btnenviarmail);
        mail.setOnClickListener(v -> {
            // Crear un intent implícito para abrir un mail
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("abc@gmail.com"));
            intent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
            intent.putExtra(Intent.EXTRA_CC, new String[]{"xyz@gmail.com"});
            intent.putExtra(Intent.EXTRA_BCC, new String[]{"pqr@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "your subject goes here...");
            intent.putExtra(Intent.EXTRA_TEXT, "Your message content goes here...");

            startActivity(intent);
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
        config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),ConfigActivity.class);
                configLauncher.launch(i);
            }
        });



    }
}