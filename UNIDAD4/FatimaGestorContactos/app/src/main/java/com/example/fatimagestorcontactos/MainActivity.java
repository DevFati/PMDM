package com.example.fatimagestorcontactos;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.Manifest;
import android.widget.Toast;

import com.example.fatimagestorcontactos.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final int SOLICITUD_PERMISO_CONTACTOS = 100;

    private ActivityMainBinding binding;

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

        
        binding.buttonContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    // Permisos ya concedidos, cargar fragmento
                    cargarFragmento(new ContactListFragment());
                } else {
                    // Solicitar permisos
                    Toast.makeText(MainActivity.this, "Permiso denegado para acceder a los contactos", Toast.LENGTH_SHORT).show();

                    solicitarPermisosC();
                }



            }
        });

        /*
        binding.buttonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarFragmento(new AddContactFragment());
            }
        });

        binding.buttonBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarFragmento(new BluetoothFragment());
            }
        });

*/


    }

    private void solicitarPermisosC() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, SOLICITUD_PERMISO_CONTACTOS);

    }

    private void cargarFragmento(Fragment fragmento) {
        FragmentManager ad=getSupportFragmentManager();

        ad.beginTransaction()
                .replace(binding.fragmentContainer.getId(), fragmento)
                .commit();
    }
}