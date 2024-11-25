package com.example.notificacionespersonalizadas;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NotificacionPulsada extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion_pulsada);
        int valorBrillo=getIntent().getIntExtra("valorBrillo",-1);

        TextView t=findViewById(R.id.textView);
        t.setText("El brillo actual es: "+valorBrillo);
    }
}