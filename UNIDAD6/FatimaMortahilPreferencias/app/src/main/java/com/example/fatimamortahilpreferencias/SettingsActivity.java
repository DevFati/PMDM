package com.example.fatimamortahilpreferencias;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences misPreferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Habilitar el ActionBar con botón para regresar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicializar SharedPreferences y registrar el listener
        misPreferencias = PreferenceManager.getDefaultSharedPreferences(this);
        misPreferencias.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Volver a la actividad anterior
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Desregistrar el listener para evitar fugas de memoria
        if (misPreferencias != null) {
            misPreferencias.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("dark_mode")) {
            boolean isDarkMode = sharedPreferences.getBoolean(key, false);
            // Aplicar el modo oscuro en tiempo real
            AppCompatDelegate.setDefaultNightMode(isDarkMode ?
                    AppCompatDelegate.MODE_NIGHT_YES :
                    AppCompatDelegate.MODE_NIGHT_NO);

            Toast.makeText(this, "Modo oscuro: " + (isDarkMode ? "Activado" : "Desactivado"), Toast.LENGTH_SHORT).show();
        } else if (key.equals("phone_format")) {
            String phoneFormat = sharedPreferences.getString(key, "+34 (España)");
            Toast.makeText(this, "Formato de teléfono actualizado: " + phoneFormat, Toast.LENGTH_SHORT).show();
        } else if (key.equals("notifications")) {
            if (PermissionsManager.comprobarPermisosNotificaciones(getApplicationContext())) {
                boolean notifications = sharedPreferences.getBoolean(key, false);
                if (notifications) {
                    Toast.makeText(this, "Notificaciones activadas", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Notificaciones desactivadas", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Preferencia " + key + " actualizada", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

