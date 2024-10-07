package edu.pmdm.miprimeraactivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View .OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //mantener aqui nuestro código

        Button b=(Button)findViewById(R.id.button);
        b.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        TextView t=(TextView)findViewById(R.id.textView2);
        t.setText("Pulsado");
    }
}