package com.example.fatimamortahilpreferencias;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private SwitchPreferenceCompat notificationsPref;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Referencia al switch de notificaciones
        notificationsPref = findPreference("notifications");

        // Configurar permisos para notificaciones
        configurarPermisosNotificaciones();

    }

    private void configurarPermisosNotificaciones() {
        if (notificationsPref != null) {
            // Si no tiene permisos, desactivar notificaciones
            if (!PermissionsManager.comprobarPermisosNotificaciones(requireContext())) {
                notificationsPref.setChecked(false);
            }

            // Listener para manejar clics en el switch
            notificationsPref.setOnPreferenceClickListener(preference -> {
                if (!PermissionsManager.comprobarPermisosNotificaciones(requireContext())) {
                    // Solicitar permisos si no están otorgados
                    notificationsPref.setChecked(false);
                    PermissionsManager.pedirPermisosNotificaciones(requireActivity());
                }
                return true;
            });
        }
    }
}


