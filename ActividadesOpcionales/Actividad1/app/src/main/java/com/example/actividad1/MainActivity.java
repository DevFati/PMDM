package com.example.actividad1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
       //mantener aqui nuestro codigo
        Button num1=(Button)findViewById(R.id.btn1);
        num1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t=(TextView)findViewById(R.id.txt1);

                t.setText("Has pulsado el boton 1");

            }
        });

        Button num2=(Button)findViewById(R.id.btn2);
        num2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t=(TextView)findViewById(R.id.txt1);

                t.setText("Has pulsado el boton 2");

            }
        });

    }



}