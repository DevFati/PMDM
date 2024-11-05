package es.riberadeltajo.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //declara el activityresultlauncher que lo usaremos para iniciar la segunda actividad
    private ActivityResultLauncher<Intent> secondActivityLauncher;
    //para ver que provincia se selecciono
    public final int SELECCION_PROVINCIA=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //aqui se establece el diseño de la actividad
        setContentView(R.layout.activity_main);

        //Inicializar el ActivityResultLauncher
        secondActivityLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), //se especifica el contrato para que se inicie la actividad y esperar un resultado
                new ActivityResultCallback<ActivityResult>() { //implementación de la interfaz para que reciba el resultado
                    @Override
                    public void onActivityResult(ActivityResult result) { //metodo llamado cuando se recibe el resultado de la actividad
                        if(result.getResultCode()==RESULT_OK){ //verifica si esta bien el resultado
                            Intent data=result.getData(); //obtiene la intencion devuelta
                            if(data!=null){ //comprueba que no sea nula
                                //extrae eñ dato que se pasó de la segunda actividad
                                String retorno=data.getStringExtra("SELECCION");
                                //muestra un toast con un mensaje con la selección del usuario
                                Toast.makeText(MainActivity.this,"El usuario seleccionó "+retorno,Toast.LENGTH_LONG).show();
                                //encuentra el textview en el diseño y establece su texto
                                TextView t=findViewById(R.id.textView);
                                t.setText(retorno); //muestra la selección en el textview
                            }

                        }
                    }
                }

        );




        Button b=findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Usar el launcher enn lugar de strarActivityForResult
                Intent i=new Intent(getApplicationContext(),SecondActivity.class);
                secondActivityLauncher.launch(i);
            }
        });
    }


}