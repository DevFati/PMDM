package com.example.prc3_fatimamortahil;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.prc3_fatimamortahil.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
private ActivityMainBinding binding; //"binding" conectaria el código java con el diseño XML usando "view binding".
private int Permiso_Maps=1; // usamos este numero para verificar los permisos del mapa
private LocationManager l; // l sería el gestor con el que manejaremos GPs y obtendremos la ubicacion
private double Latitud,Longitud;
private boolean modoOscuro; //Esta variable nos dira si el usuario activo el modo Oscuro o no.
private SharedPreferences preferences;
private static boolean permisosSolicitados=false; //Para evitar pedir permisos reiteradamente de manera innecesaria.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View vista=binding.getRoot();
        EdgeToEdge.enable(this);
        setContentView(vista);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Abrimos las preferencias guardadads y vemos si el modo oscuro esta activado
        //Si no hay info guardada, el modo sera claro.
        preferences = getSharedPreferences("ajustes", MODE_PRIVATE);
        modoOscuro = preferences.getBoolean("modoOscuro", false);
        //Si el modo oscuro estaba activado entonces reponemos el tema. Si no, entonces el tema sera claro.
        if (modoOscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

//Boton para cambiar de tema
        binding.buttonTema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarTema();
            }
        });




        //Inicializamos el gestor de ubicación
        l=(LocationManager)getSystemService(LOCATION_SERVICE);

        //Transiciones entre actividades para cuando el usuario cambia de una pantalla a otra
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        //Comprobamos si la latitud y la longitud existen de antes para mostrarlas en pantalla.
        if(Latitud!=0 && Longitud!=0){
            binding.textViewDireccion.setText("Latitud: " + Latitud + "\nLongitud: " + Longitud);
        }

        //Verificamos permisos de ubicación.
        //Si aún no hemos pedido permisos (esten aceptados o no)
        //los pedimos con "verificacionPermisos". Si ya los pedimos,
        //obtenemos directamente la ubicacion
        if (!permisosSolicitados) {
            verificacionPermisos();
            permisosSolicitados = true; // Evita volver a solicitar permisos.
        }else{
            obtenerUbicacion();
        }

        //Botón para iniciar sesión
        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validamos correo
                if(!Patterns.EMAIL_ADDRESS.matcher(binding.editTextEmail.getText()).matches()){
                    Toast.makeText(MainActivity.this,"Email incorrecto",Toast.LENGTH_SHORT).show();

                    return; //No seguimos
                }
                //Se revisa que el usuario dio permiso para usar GPS. Si no, mostramos un
                //mensaje y no seguimos.
                if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(MainActivity.this,"Permisos denegados",Toast.LENGTH_SHORT).show();
                    return; //No seguimos
                }

                //Si no hay coordenads de ubicación tampoco se sigue
                //Hacemos esto porque al usar coordenadas precisas, tardan un poco en aparecer
                //limitando el programa de esta forma nos permite no dejar el usuario seguir
                //aunque todos los permisos esten dados si todavía no aparecieron
                //la longitud y latitud.
                if(Longitud==0 || Latitud==0){
                    return; //No seguimos
                }
                //Si todo esta correcto, llevamos al usuario a la pantalla "BikeActivity"

                Intent intent=new Intent(MainActivity.this,BikeActivity.class);
                startActivity(intent);
            }
        });

        //Boton para abrir ubicación en google Maps.
        binding.imageButtonDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String direccion=binding.textViewDireccion.getText().toString();
                //Si la ubicacion es incorrecta entonces mostramos un mensaje de error y no hacemos nada más
                if(!direccion.startsWith("Latitud")){
                    Toast.makeText(MainActivity.this, "Comprueba permisos y vuelvelo a intentar", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Construimos URI para google maps

                String uri="geo:"+Latitud+","+Longitud;
               // System.out.println(uri);
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

                //Vemos si hay google maps en el movil/emulador para manejar el intent

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);

                }else{
                    Toast.makeText(MainActivity.this, "Asegurese de tener Google Maps instalado", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void verificacionPermisos() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si no hay permiso, lo solicitamos
            //OJO, importante importar el Manifest (sino me da opcion, hay que hacerlo a mano)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Permiso_Maps);
        } else {
            // Si ya se otorgaron permisos (sea que se acepto o no), obtenemos la ubicación o mensaje de permiso denegado
            obtenerUbicacion();
        }
    }

    private void obtenerUbicacion() {
        // Verificamos de nuevo si  el permiso fue otorgado (estamos usando Fine_Location que nos da una ubicación precisa)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
           Toast.makeText(MainActivity.this,"Obteniendo ubicación, sea paciente",Toast.LENGTH_SHORT).show();
            // Solicitamos actualizaciones de ubicación
            l.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
                @Override
                public void onLocationChanged(Location ubicacion) {
                    //Cuando cambia la localización, actualizamos las coordenadas y las mostramos por pantalla.
                    Latitud = ubicacion.getLatitude();
                     Longitud = ubicacion.getLongitude();
                    binding.textViewDireccion.setText("Latitud: " + Latitud + "\nLongitud: " + Longitud);
                    // Cambiamos el texto del mensaje mostrando la ubicación
                    l.removeUpdates(this); //paramos las actualizaciones de ubicación
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }
                @Override
                public void onProviderEnabled(String provider) {
                    obtenerUbicacion();
                }
                @Override
                public void onProviderDisabled(String provider) {
                    //En caso de que aunque se conceda permiso, que este desabilitado la opcion de ubicacion (prueba realizada en movil fisico, especificamente en un samsung (Ajustes >> Ubicacion))
                    binding.textViewDireccion.setText("GPS deshabilitado");
                    Latitud=0;
                    Longitud=0;
                }
            });
        } else {
            binding.textViewDireccion.setText("Permisos no concedidos");
        }
    }
    @Override

    public void onRequestPermissionsResult(int requestCode, String[] permisos, int[] resultados) {
        super.onRequestPermissionsResult(requestCode, permisos, resultados);
        if (requestCode == Permiso_Maps) {
            // Si el permiso fue otorgado, obtenemos la ubicación
            if (resultados.length > 0 && resultados[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion();
            } else {
                //si no pues mostramos mensaje de permiso denegado
                binding.textViewDireccion.setText("Permiso denegado");
            }
        }
    }


    private void cambiarTema(){
        //Creamos un cuadro de dalogo para que el usuario pueda elegir entre un modo
        //claro u oscuro. Guardamos su seleccion y actualizamos la pantalla
        //para que se muestre el cambio aplicado.
        int selectedOption;
        if (modoOscuro) {
            selectedOption = 1; // Oscuro
        } else {
            selectedOption = 0; // Claro
        }

        new AlertDialog.Builder(this)
                .setTitle("Cambiar Tema")
                .setSingleChoiceItems(new String[]{"Modo Claro", "Modo Oscuro"}, selectedOption, (dialog, which) -> {
                    if (which == 1) {
                        modoOscuro = true;
                    } else {
                        modoOscuro = false;
                    }
                })
                .setPositiveButton("Aplicar", (dialog, which) -> {
                    if (modoOscuro) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    preferences.edit().putBoolean("modoOscuro", modoOscuro).apply();
                    recreate();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //verificamos si se concediron los permisos manualmente

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Si los permisos están concedidos, obtenemos la ubicación
            obtenerUbicacion();
        } else {
            binding.textViewDireccion.setText("Permisos no concedidos");
            Latitud = 0;
            Longitud = 0;
        }

    }
}