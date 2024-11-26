package edu.pmdm.permisomaps;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.Manifest;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.location.LocationListener;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    public final int Permiso_Maps=0;
    private LocationManager l;
    private TextView textV;
    private Button ubi;
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
        textV=findViewById(R.id.textViewMensaje);
        ubi=findViewById(R.id.btnUbi);

        // Inicializamos el gestor de ubicación
        l = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Configuramos el botón para verificar permisos al hacer clic
        ubi.setOnClickListener(v -> verificarPermiso());
    }

    private void verificarPermiso() {
        // Verificamos si el permiso ya ha sido concedido
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si no hay permiso, lo solicitamos
            //OJO, importante importar el Manifest (sino me da opcion, hay que hacerlo a mano)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Permiso_Maps);
        } else {
            // Si ya se otorgaron permisos (sea que se acepto o no), obtenemos la ubicación o mensaje de permiso denegado
            obtenerUbicacion();
        }
    }

    private void obtenerUbicacion() {
        // Verificamos de nuevo si  el permiso fue otorgado
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Solicitamos actualizaciones de ubicación
            l.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
                @Override
                public void onLocationChanged(Location ubicacion) {
                    double latitud = ubicacion.getLatitude();
                    double longitud = ubicacion.getLongitude();
                    textV.setText("Latitud: " + latitud + "\nLongitud: " + longitud);
                    // Cambiamos el texto del mensaje mostrando la ubicación
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) { }

                @Override
                public void onProviderEnabled(String provider) { }

                @Override
                public void onProviderDisabled(String provider) {
                    //En caso de que aunque se conceda permiso, que este desabilitado la opcion de ubicacion (prueba realizada en movil fisico, especificamente en un samsung (Ajustes >> Ubicacion))
                    textV.setText("GPS deshabilitado");
                }
            });
        } else {
            textV.setText("Permisos no concedidos");
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
                textV.setText("Permiso denegado");
            }
        }
    }
}