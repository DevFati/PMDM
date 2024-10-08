package com.example.actividad2;

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
      //  EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //mantener aqui nuestro codigo
        Button operacion=(Button)findViewById(R.id.btnCalcular);
        operacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t =(TextView)findViewById(R.id.edttxtPosicion);
                if(esNumero((String)t.getText())){
                    int num=Integer.parseInt((String)t.getText());

                    int c=1;
                    int divisores=0;
                    while(c!=num){
                        for(int i=1;i<c;i++){
                            if(i%c==0){
                                divisores++;
                            }
                        }

                        if(divisores==2){
                            c++;
                        }
                        divisores=0;
                    }

                    TextView g =(TextView)findViewById(R.id.txtResultado);
                    g.setText(c);

                }else{
                    TextView y =(TextView)findViewById(R.id.txtResultado);
                    y.setText("Introduce un número entero valido");
                }
            }
        });
    }

    private boolean esNumero(String numero) {

        if(numero.matches("[1-9]+")){
            return true;
        }else{

            return false;
        }


    }
}