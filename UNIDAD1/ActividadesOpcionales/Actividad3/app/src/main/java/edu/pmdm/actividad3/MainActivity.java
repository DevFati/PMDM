package edu.pmdm.actividad3;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button f=(Button)findViewById(R.id.btnFahr);

        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText t=(EditText) findViewById(R.id.edittxtCent);

                if(esNumero(t.getText().toString())){
                    TextView y=(TextView) findViewById(R.id.txtResultado);
                    Double num=Double.parseDouble(t.getText().toString());
                    double resultado=(num*9/5)+32;
                    y.setText(resultado+"");
                }else{
                    TextView y=(TextView) findViewById(R.id.txtResultado);
                    y.setText("Introduce un número valido");
                }



            }
        });


        Button k=(Button)findViewById(R.id.btnKelvin);

        k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText t=(EditText) findViewById(R.id.edittxtCent);

                if(esNumero(t.getText().toString())){
                    TextView y=(TextView) findViewById(R.id.txtResultado);
                    double num=Double.parseDouble(t.getText().toString());
                    double resultado=(num+273.15);
                    y.setText(resultado+"");
                }else{
                    TextView y=(TextView) findViewById(R.id.txtResultado);
                    y.setText("Introduce un número valido");
                }



            }
        });

        Button r=(Button)findViewById(R.id.btnRank);

        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText t=(EditText) findViewById(R.id.edittxtCent);

                if(esNumero(t.getText().toString())){
                    TextView y=(TextView) findViewById(R.id.txtResultado);
                    double num=Double.parseDouble(t.getText().toString());
                    double resultado=(num*9/5+491.67);
                    y.setText(resultado+"");
                }else{
                    TextView y=(TextView) findViewById(R.id.txtResultado);
                    y.setText("Introduce un número valido");
                }



            }
        });


    }

    private  boolean esNumero(String numero) {


        if (numero.matches("-?[0-9]+(\\.[0-9]+)?")) {
            return true;
        } else {

            return false;
        }

    }


}