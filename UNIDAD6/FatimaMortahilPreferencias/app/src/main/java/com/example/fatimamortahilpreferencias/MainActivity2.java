package com.example.fatimamortahilpreferencias;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class MainActivity2 extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences misPreferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

         misPreferencias = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        misPreferencias.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //"es feedback" no "feeback" esta mal y eso ocasiona que cuando se de a "siguiente" y se seleccionen todas ls preferencias y se vaya para atras y se pulsen en los tres puntitos
        //la aplicacion peta
        if (key.equals("feedback") || key.equals("tipoEmpleado"))
            Toast.makeText(this, "Preferencia " + key + " actualizada: " + sharedPreferences.getString(key, ""), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Preferencia " + key + " actualizada: " + sharedPreferences.getBoolean(key, false), Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desregistrar el listener para evitar que la actividad escuche cambios después de ser destruida porque si la dejamos, entrara en conflicto
        // con las preferencias creadas por mi.
        if (misPreferencias != null) {
            misPreferencias.unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}
