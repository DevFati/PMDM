package edu.pmdm.cronometrotabata;

import android.graphics.Color;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    //Declaración de variables para las vistas y recursos.

    private ConstraintLayout mainLayout; //Layout principal
    private TextView txtEstado,txtContador,txtSeriesLeft; //Vistas de texto
    private EditText edtxtSeries,edtxtTrabajo, edtxtDescanso; //Campos de entrada
    private MediaPlayer sonidoBeep, sonidoGong; //Sonidos para el entrenamiento



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

        //Inicializar las vistas
        mainLayout = findViewById(R.id.main);
        txtEstado = findViewById(R.id.txtWR);
        txtContador=findViewById(R.id.txtContador);
        txtSeriesLeft=findViewById(R.id.txtSeriesL);
        edtxtSeries = findViewById(R.id.edtxtSeries);
        edtxtTrabajo=findViewById(R.id.edtxtTrabajo);
        edtxtDescanso=findViewById(R.id.edttxtDescanso);

        //Configurar el botón de inicio.
        ImageButton btnStart=(ImageButton) findViewById(R.id.imgbtn);


        btnStart.setOnClickListener(new View.OnClickListener() { //Usar clase anónima
            @Override
            public void onClick(View view) {

                startCycle(); //Llama al método de inicio al hacer clic

            }
        });
    }

    private void startCycle() {
        //Lo que hace este método es hacer comprobaciones para ver si el usuario introdujo los
        //datos correctamente si es así, llama al método cicloSesion para iniciar la actividad. En caso contrario le lanza mensajes de error.

        // Desactivar el botón durante el entrenamiento
        ImageButton btnStart = findViewById(R.id.imgbtn);
        btnStart.setEnabled(false);


        //Validar campos de entrada
        if(TextUtils.isEmpty(edtxtSeries.getText())|| TextUtils.isEmpty(edtxtTrabajo.getText())|| TextUtils.isEmpty(edtxtDescanso.getText())){
            btnStart.setEnabled(true); //Rehabilitar el botón si hay campos vacíos.
            Toast.makeText(this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show(); //Mensaje emergente
            return; //Si el método startCycle() encuentra que alguno de los campos
            //está vacío, ejecuta "return;", lo que significa que no continuará
            //procesando el resto del código en startCycle();
        }
        //Convertir los textos a enteros
        int series=Integer.parseInt(edtxtSeries.getText().toString());
        int tiempoTrabajo=Integer.parseInt(edtxtTrabajo.getText().toString());
        int tiempoDescanso=Integer.parseInt(edtxtDescanso.getText().toString());

        //Validar que los valores sean mayores que cero
        if(series==0|| tiempoTrabajo==0|| tiempoDescanso==0){
            btnStart.setEnabled(true); //Rehabilitar el botón si hay valores inválidos
            Toast.makeText(this, "Los valores deben ser mayores que cero.", Toast.LENGTH_SHORT).show(); //Mensaje emergente
            return;
            //Si el método startCycle() encuentra que alguno de los campos
            //es 0 ejecuta el "return;", lo que significa que no continuará
            //procesando el resto del código en startCycle();
        }

        //Iniciar el ciclo de sesiones
        cicloSesion(series,tiempoTrabajo,tiempoDescanso);

    }

    public void cicloSesion (int series, int tiempoTrabajo, int tiempoDescanso){
       //Actualizar la interfaz para la fase de trabajo.

        txtEstado.setText("WORK");
        txtSeriesLeft.setText("Series Left: "+series + "");
        mainLayout.setBackgroundResource(R.drawable.degradado_verde); //Cambiar fondo a verde

        //Inicializar sonidos

        MediaPlayer sonidoBeep= MediaPlayer.create(this,R.raw.beep);
        MediaPlayer sonidoGong= MediaPlayer.create(this,R.raw.gong);
        sonidoBeep.start(); //Reproducir sonido de inicio.

        //Contador para la fase de trabajo.
        
        new CountDownTimer((tiempoTrabajo * 1000), 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                txtContador.setText((millisUntilFinished / 1000) + ""); //Mostrar tiempo restante
                //lo concateno a una cadena vacía para convertirlo a un String para que se pueda mostrar.

            }

            @Override
            public void onFinish() {

                //Cambiar a la fase de descanso

                txtEstado.setText("REST");
                mainLayout.setBackgroundResource(R.drawable.degradado_rojo); //Cambiar fondo a rojo

                //Contador para la fase de descanso
                new CountDownTimer((tiempoDescanso * 1000), 1000) {

                    @Override
                    public void onTick(long l) {


                        txtContador.setText((l / 1000) + "");//Mostrar tiempo restante
                        //lo concateno a una cadena vacía para convertirlo a un String para que se pueda mostrar.
                    }

                    @Override
                    public void onFinish() {
                        //Si quedan series, continuar el ciclo
                        if(series>1){
                            cicloSesion((series-1),tiempoTrabajo,tiempoDescanso);
                        }else{
                            //Finalizar entrenamiento
                            ImageButton btnStart=(ImageButton) findViewById(R.id.imgbtn);
                            btnStart.setEnabled(true); //Rehabilitar el botón
                            sonidoGong.start(); //Reproducir sonido de finalización
                            mainLayout.setBackgroundColor(Color.WHITE); //Restablecer fondo a blanco

                            txtEstado.setText("FINISHED!"); //Mensaje de finalización

                            txtSeriesLeft.setText("Series Left: 0"); //Actualizar series restantes
                        }


                    }
                }.start();

            }
        }.start();
    };

}