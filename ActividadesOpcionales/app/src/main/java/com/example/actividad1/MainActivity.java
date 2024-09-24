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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
      //mantener aqui nuestro codigo
        Button num1=(Button)findViewById(R.id.button);
        num1.setOnClickListener(this);

        Button num2=(Button)findViewById(R.id.button2);
        num2.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        TextView t=(TextView)findViewById(R.id.textView2);
        if(v.getId()==R.id.button){
            t.setText("Has pulsado el boton 1");
        }else{
            t.setText("Has pulsado el boton 2");
        }

    }


}