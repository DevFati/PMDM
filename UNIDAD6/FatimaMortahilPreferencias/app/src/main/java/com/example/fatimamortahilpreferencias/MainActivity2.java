package com.example.fatimamortahilpreferencias;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class MainActivity2 extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        SharedPreferences misPreferencias = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        misPreferencias.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("feeback") || key.equals("tipoEmpleado"))
            Toast.makeText(this, "Preferencia " + key + " actualizada: " + sharedPreferences.getString(key, ""), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Preferencia " + key + " actualizada: " + sharedPreferences.getBoolean(key, false), Toast.LENGTH_SHORT).show();

    }
}