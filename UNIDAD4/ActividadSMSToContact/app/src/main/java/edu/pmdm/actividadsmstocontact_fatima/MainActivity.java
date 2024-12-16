package edu.pmdm.actividadsmstocontact_fatima;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.pmdm.actividadsmstocontact_fatima.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final int SOLICITUD_PERMISO_CONTACTOS = 100;
    private static final int SOLICITUD_PERMISO_SMS = 101;
    private List<Contacto> listaContactos;
    private Cadapter adapter;
    private static ActivityMainBinding binding;
    public static  Contacto contactoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        binding=ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, SOLICITUD_PERMISO_CONTACTOS);
        }
        //permiso de contactos


        binding.btnEnviar.setEnabled(false);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            binding.btnSeleccionarC.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(this, "Permisos de contacto denegados", Toast.LENGTH_SHORT).show();

        }
       binding.btnSeleccionarC.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               binding.edtxtNombre.setVisibility(View.VISIBLE);
               binding.edtxtApellido.setVisibility(View.VISIBLE);
               binding.btnBuscarNom.setVisibility(View.VISIBLE);
               binding.btnBuscarApe.setVisibility(View.VISIBLE);
           }
       });

        listaContactos = new ArrayList<>();
        adapter = new Cadapter(listaContactos, new Cadapter.OnContactLongClickListener() {
            @Override
            public void onContactLongClick(Contacto contact) {

                contactoSeleccionado = contact; // Guardar el contacto seleccionado
                mostrarContenedorMensaje(contact);
            //    System.out.println("entro");
            //    Toast.makeText(MainActivity.this,"Seleccionaste "+contactoSeleccionado.getNombre(),Toast.LENGTH_SHORT).show();

            }
        });


        binding.recyclerViewContactos.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewContactos.setAdapter(adapter);

        // Cargar todos los contactos al inicio



        binding.btnBuscarNom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                filtrarContactos();
                binding.recyclerViewContactos.setVisibility(View.VISIBLE);



            }
        });


        binding.btnBuscarApe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                filtrarContactos();
                binding.recyclerViewContactos.setVisibility(View.VISIBLE);
            }
        });

        binding.btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoConfirmacion();
            }
        });

        binding.mensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                boolean contieneCaracterEspecial = contieneCaracterEspecial(s.toString());

                if (contieneCaracterEspecial) {
                    binding.contador.setText(length + "/70");
                    binding.btnEnviar.setEnabled(length > 0 && length <= 70);
                } else {
                    binding.contador.setText(length + "/160");
                    binding.btnEnviar.setEnabled(length > 0 && length <= 160);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean contieneCaracterEspecial = contieneCaracterEspecial(s.toString());

                if ((contieneCaracterEspecial && s.length() > 70) || (!contieneCaracterEspecial && s.length() > 160)) {
                    s.delete(contieneCaracterEspecial ? 70 : 160, s.length());
                }

                // Desactivar botón si el mensaje está vacío
                binding.btnEnviar.setEnabled(!s.toString().trim().isEmpty());
            }
        });
    }

    private boolean contieneCaracterEspecial(String mensaje) {
        for (int i = 0; i < mensaje.length(); i++) {
            if (!Character.isLetterOrDigit(mensaje.charAt(i)) && !Character.isSpaceChar(mensaje.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static void mostrarContenedorMensaje(Contacto contact) {

        binding.mensaje.setVisibility(View.VISIBLE);
        binding.btnEnviar.setVisibility(View.VISIBLE);
        binding.contador.setVisibility(View.VISIBLE);
        binding.contador.setText("0/160"); // Reinicia el contador
        binding.mensaje.setText(""); // Limpia el campo de mensaje
    }






    private void filtrarContactos() {
        listaContactos.clear();
        String nombreFiltro = binding.edtxtNombre.getText().toString().trim();
        String apellidoFiltro = binding.edtxtApellido.getText().toString().trim();

        String nombreQuery = "%";
        String apellidoQuery = "%";

        if (!nombreFiltro.isEmpty()) {
            nombreQuery = nombreFiltro.replace("%", "%") + "%";
        }

        if (!apellidoFiltro.isEmpty()) {
            apellidoQuery = apellidoFiltro.replace("%", "%") + "%";
        }

        ContentResolver contentResolver = getContentResolver();
        String selection;
        String[] selectionArgs;

        if (!nombreFiltro.isEmpty() && !apellidoFiltro.isEmpty()) {
            selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ? AND " +
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?";
            selectionArgs = new String[]{nombreQuery, apellidoQuery};
        } else if (!nombreFiltro.isEmpty()) {
            selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?";
            selectionArgs = new String[]{nombreQuery};
        } else if (!apellidoFiltro.isEmpty()) {
            selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?";
            selectionArgs = new String[]{apellidoQuery};
        } else {
            selection = null;
            selectionArgs = null;
        }

        Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Photo.PHOTO_URI
                },
                selection,
                selectionArgs,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String numero = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                listaContactos.add(new Contacto(nombre, numero));
            }
            cursor.close();
        }

        adapter.notifyDataSetChanged();
    }


    private void actualizarLista(List<Contacto> contactosFiltrados) {
        adapter = new Cadapter(contactosFiltrados, contact -> {
        });
        binding.recyclerViewContactos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    private void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialogo, null);
        builder.setView(dialogView);

        ImageView ivFoto = dialogView.findViewById(R.id.ivFoto);
        TextView tvNombre = dialogView.findViewById(R.id.tvNombre);
        TextView tvNumero = dialogView.findViewById(R.id.tvNumero);
        TextView tvMensaje = dialogView.findViewById(R.id.tvMensaje);

        tvNombre.setText(contactoSeleccionado.getNombre());
        tvNumero.setText(contactoSeleccionado.getNumero());
        tvMensaje.setText(binding.mensaje.getText().toString());

        if (contactoSeleccionado.getFoto() != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(Uri.parse(contactoSeleccionado.getFoto()));
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ivFoto.setImageBitmap(bitmap);
            } catch (Exception e) {
                ivFoto.setImageResource(R.drawable.baseline_account_circle_24); // Imagen por defecto
            }
        } else {
            ivFoto.setImageResource(R.drawable.baseline_account_circle_24); // Imagen por defecto
        }

        // Crear el cuadro de diálogo
        AlertDialog dialog = builder.create();

        // Configurar el botón "Volver"
        Button volver = dialogView.findViewById(R.id.btnVolver);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Cierra el cuadro de diálogo
            }
        });

        dialog.show();
    }



}