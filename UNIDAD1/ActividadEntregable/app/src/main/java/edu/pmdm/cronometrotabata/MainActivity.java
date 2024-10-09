package edu.pmdm.cronometrotabata;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

        ImageButton boton=(ImageButton) findViewById(R.id.imgbtn);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText series=(EditText) findViewById(R.id.edtxtSeries);
                EditText trabajo=(EditText) findViewById(R.id.edtxtTrabajo);
                EditText descanso=(EditText) findViewById(R.id.edttxtDescanso);

                int s=Integer.parseInt(series.getText().toString());
                int t=Integer.parseInt(trabajo.getText().toString());
                int d=Integer.parseInt(descanso.getText().toString());

                new CountDownTimer(((((t+d)*s)*1000)),1000){

                    @Override
                    public void onTick(long l) {
                        int contador=0;
                        int temp=t;
                        int des=d;
                        int ser=s;

                        while(contador!=(t+d)){

                            if(contador<=t){
                                TextView estado=(TextView) findViewById(R.id.txtWR);
                                estado.setText("WORK");

                                TextView cont=(TextView) findViewById(R.id.txtContador);
                                cont.setText((temp*1000)+"");

                                TextView seriess=(TextView) findViewById(R.id.txtSeriesL);
                                seriess.setText(ser+"");
                                temp--;
                            }else{
                                TextView estado=(TextView) findViewById(R.id.txtWR);
                                estado.setText("REST");

                                TextView cont=(TextView) findViewById(R.id.txtContador);
                                cont.setText((des*1000)+"");

                                TextView seriess=(TextView) findViewById(R.id.txtSeriesL);
                                seriess.setText(ser+"");
                                des--;
                            }

                            contador++;

                        }
                        ser--;

                    }

                    @Override
                    public void onFinish() {

                    }
                }.start();
            }
        });




    }


}