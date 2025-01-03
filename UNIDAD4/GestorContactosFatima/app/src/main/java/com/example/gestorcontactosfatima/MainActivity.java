package com.example.gestorcontactosfatima;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.gestorcontactosfatima.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final int SOLICITUD_PERMISO_CONTACTOS = 100;
    private ActivityMainBinding binding;

    private BluetoothAdapter bluetoothAdapter;

    private ActivityResultLauncher<Intent> activarBluetoothLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Registrar el ActivityResultLauncher
        activarBluetoothLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Toast.makeText(this, "Bluetooth activado", Toast.LENGTH_SHORT).show();
                        solicitarPermisoUbicacion();

                    } else {
                        Toast.makeText(this, "Bluetooth no activado", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        binding.buttonContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verifica si el permiso ya está concedido
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido, cargar el fragmento
                    cargarFragmento(new ContactListFragment());
                } else {
                    // Solicitar el permiso
                    solicitarPermisosC();
                }
            }
        });


        binding.buttonBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manejarBluetooth();
            }
        });


        binding.buttonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarFragmento(new AddContactFragment());
            }
        });





    }

    private void manejarBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            // Crear Intent para activar Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activarBluetoothLauncher.launch(enableBtIntent);
        } else {
            // Si Bluetooth ya está activado, solicitar permisos de ubicación
            solicitarPermisoUbicacion();
        }
    }

    private void solicitarPermisoUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permiso ya otorgado, abrir lista de dispositivos
            abrirListaDispositivos();
        } else {
            // Solicitar permiso
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    101 // Código de solicitud de permiso
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso otorgado, abrir lista de dispositivos
                abrirListaDispositivos();
            } else {
                // Permiso denegado
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void solicitarPermisosC() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            // Mostrar explicación al usuario antes de solicitar el permiso
            Toast.makeText(this, "Se necesita acceso a los contactos para mostrar la lista.", Toast.LENGTH_SHORT).show();
        }

        // Solicitar permisos
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, SOLICITUD_PERMISO_CONTACTOS);
    }

    private void cargarFragmento(Fragment fragmento) {
        FragmentManager ad=getSupportFragmentManager();

        ad.beginTransaction()
                .replace(binding.fragmentContainer.getId(), fragmento)
                .commit();
    }



    private void abrirListaDispositivos() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new BluetoothFragment())
                .addToBackStack(null)
                .commit();
    }






}