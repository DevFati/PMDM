package com.example.fatimamortahilpreferencias;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    //Identificador del canal de las notificaciones
    public static final String CHANNEL_ID = "USER_DATA_CHANNEL";

    //Variables para los campos de texto donde el usuario escriba los datos
    EditText edNombre;
    EditText edEmail;
    EditText edEmpresa;
    EditText edEdad;
    EditText edSueldo;
    SharedPreferences misPreferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Crea el canal de notificaciones
        createNotificationChannel(this);
        // antes de que se muestre la pantalla, aplicamos el modo oscuro si es que el usuariio lo activo
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ?
                AppCompatDelegate.MODE_NIGHT_YES :
                AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edNombre = findViewById(R.id.edNombre);
        edEmail = findViewById(R.id.edEmail);
        edEmpresa = findViewById(R.id.edEmpresa);
        edEdad = findViewById(R.id.edEdad);
        edSueldo = findViewById(R.id.edSueldo);

        //Obtengo las preferencias del dato de usuario guardada
        misPreferencias = getSharedPreferences("Basic Data", MODE_PRIVATE);

        String nombre = misPreferencias.getString("nombre", "");
        actualizarBienvenida();
        String empresa = misPreferencias.getString("empresa", "Ribera del Tajo");
        String email = misPreferencias.getString("email", "cambiame@riberadeltajo.es");
        int edad = misPreferencias.getInt("edad", 18);
        float sueldo = misPreferencias.getFloat("sueldo", 15000);

        edNombre.setText(nombre);
        edEmpresa.setText(empresa);
        edEmail.setText(email);
        edSueldo.setText(String.valueOf(sueldo));
        edEdad.setText(String.valueOf(edad));

        //Cuando el usuario haga clic en "Guardar"
        Button btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onClick(View v) {
                //Guardamos los datos en las preferencias
                SharedPreferences.Editor editor = misPreferencias.edit();
                //Obtenemos lo que el usuario ha escrito
                String nombre = edNombre.getText().toString();
                String empresa = edEmpresa.getText().toString();
                String email = edEmail.getText().toString();
                String edadStr = edEdad.getText().toString();
                String sueldoStr = edSueldo.getText().toString();

                // Validamos que los campos no estén vacíos
                if (nombre.isEmpty() || email.isEmpty() || empresa.isEmpty() || edadStr.isEmpty() || sueldoStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show();
                    return ;
                }

                // Validamos que el nombre solo contenga letras
                if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
                    Toast.makeText(MainActivity.this, "El nombre solo puede contener letras.", Toast.LENGTH_SHORT).show();
                    return ;
                }

                try {
                    // Validamos edad
                    int edad = Integer.parseInt(edadStr);
                    if (edad < 1 || edad > 100) {
                        Toast.makeText(MainActivity.this, "La edad debe estar entre 1 y 100.", Toast.LENGTH_SHORT).show();
                        return; // Salir sin guardar
                    }

                    // Validamos email
                    if (!esEmailValido(email)) {
                        Toast.makeText(MainActivity.this, "El email no es válido.", Toast.LENGTH_SHORT).show();
                        return; // Salir sin guardar
                    }

                    // Validamos el sueldo
                    if (sueldoStr.isEmpty() || !sueldoStr.matches("\\d+\\.?\\d*")) {
                        Toast.makeText(MainActivity.this, "El sueldo debe ser un número decimal válido.", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    // Guardar datos válidos
                    editor.putString("nombre", nombre);
                    editor.putString("empresa", empresa);
                    editor.putString("email", email);
                    editor.putInt("edad", edad);
                    editor.putFloat("sueldo", sueldo);

                    if (!editor.commit()) {
                        Toast.makeText(MainActivity.this, "Error al guardar las preferencias.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Datos guardados correctamente.", Toast.LENGTH_SHORT).show();
                        actualizarBienvenida(); // Actualizar el texto de bienvenida

                        //si las notificaciones estan activas, mostramos una notificacion
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        if (prefs.getBoolean("notifications", false) &&
                                PermissionsManager.comprobarPermisosNotificaciones(MainActivity.this)) {

                            // Crear intent para mostrar los detalles
                            Intent intent = new Intent(MainActivity.this, NotificationDetailsActivity.class);
                            intent.putExtra("nombre", nombre);
                            intent.putExtra("empresa", empresa);
                            intent.putExtra("email", email);
                            intent.putExtra("edad", edad);
                            intent.putExtra("sueldo", sueldo);
                            PendingIntent pendingIntent = PendingIntent.getActivity(
                                    MainActivity.this,
                                    0,
                                    intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                            );
                            // Crear la notificación
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.baseline_auto_awesome_24)
                                    .setContentTitle("Datos Guardados")
                                    .setContentText("Pulsa para ver los datos guardados.")
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);

                            // Mostrar la notificación
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                // Si el permiso no está concedido, mostrar un mensaje o manejarlo
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
                                return;
                            }
                            notificationManager.notify(1, builder.build());
                        }
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Error en la entrada de datos. Por favor, revisa los campos.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    //Boton para ir a la siguiente pantalla
        Button btnSiguiente = findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(i);
            }
        });

    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "User Data Notifications";
            String description = "Notifications for saved user data";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



    private void actualizarBienvenida() {
        // Leer el nombre desde SharedPreferences personalizadas
        String nombre = misPreferencias.getString("nombre", "Usuario");

        // Actualizar el TextView de bienvenida
        TextView txtBienvenida = findViewById(R.id.txtBienvenida);
        txtBienvenida.setText("Bienvenido, " + nombre);
    }

//validamos mail
    private boolean esEmailValido(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email != null && email.matches(emailPattern);
    }

    // Método para actualizar la interfaz al volver a esta pantalla

    @Override
    protected void onResume() {
        super.onResume();


        actualizarBienvenida();

        // Leer y actualizar otros datos si es necesario
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String phoneFormat = prefs.getString("phone_format", "+34 (España)");
        boolean notificationsEnabled = prefs.getBoolean("notifications", false);

        TextView txtFormatoTelefono = findViewById(R.id.txtFormatoTelefono);
        TextView txtNotificaciones = findViewById(R.id.txtNotificaciones);

        txtFormatoTelefono.setText("Formato: " + phoneFormat);
        txtNotificaciones.setText("Notificaciones: " + (notificationsEnabled ? "Activadas" : "Desactivadas"));
    }


    // Método para mostrar el menú de configuración

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    // Método para manejar clics en el menú

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}