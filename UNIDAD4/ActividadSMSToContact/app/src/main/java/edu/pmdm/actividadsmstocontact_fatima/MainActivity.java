package edu.pmdm.actividadsmstocontact_fatima;
import android.content.ContentResolver;
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


        //pedimos los permisos de contactos nada mas crear la app si no estan otorgados
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, SOLICITUD_PERMISO_CONTACTOS);
        }



        binding.btnEnviar.setEnabled(false);
        binding.btnSeleccionarC.setVisibility(View.VISIBLE);

       binding.btnSeleccionarC.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
               binding.edtxtNombre.setVisibility(View.VISIBLE);
               binding.edtxtApellido.setVisibility(View.VISIBLE);
               binding.btnBuscarNom.setVisibility(View.VISIBLE);
               binding.btnBuscarApe.setVisibility(View.VISIBLE);
               }else{
                   Toast.makeText(MainActivity.this, "Permisos de contacto denegados", Toast.LENGTH_SHORT).show();

               }
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
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, SOLICITUD_PERMISO_SMS);
                    Toast.makeText(MainActivity.this, "Permisos de SMS denegados", Toast.LENGTH_SHORT).show();

                } else {
                    mostrarDialogoConfirmacion();
                    enviarSms();
                    binding.mensaje.setText("");
                }
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
                //cuando se pasa de 70 o 160 lo que hago es caparlo y borrar los caracteres sobrantes y salta un mensaje de advertencia
                //informando de esto al usuario
                if (contieneCaracterEspecial) {
                    if (s.length() > 70) {
                        s.delete(70, s.length());
                        Toast.makeText(MainActivity.this, "Mensaje limitado a 70 caracteres con Unicode" , Toast.LENGTH_SHORT).show();

                    }
                } else {
                    if (s.length() > 160) {
                        s.delete(160, s.length());
                        Toast.makeText(MainActivity.this, "Mensaje limitado a 160 caracteres", Toast.LENGTH_SHORT).show();

                    }
                }

                // Desactivar botón si el mensaje está vacío
                binding.btnEnviar.setEnabled(!s.toString().trim().isEmpty());
            }
        });
    }

    private void enviarSms() {
        String mensaje = binding.mensaje.getText().toString();
        String numero = contactoSeleccionado.getNumero();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numero, null, mensaje, null, null);
            Toast.makeText(this, "SMS enviado a " + contactoSeleccionado.getNombre(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Por si acaso ocurre algun error entra aqui
            Toast.makeText(this, "Error al enviar el SMS", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private boolean contieneCaracterEspecial(String mensaje) {
        //probamos si se introdujo algun caracter especial
        for (int i = 0; i < mensaje.length(); i++) {
            if (!Character.isLetterOrDigit(mensaje.charAt(i)) && !Character.isSpaceChar(mensaje.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static void mostrarContenedorMensaje(Contacto contact) {
        //habilitamos los diferentes campos y reiniciamos el campo y el contador
        binding.mensaje.setVisibility(View.VISIBLE);
        binding.btnEnviar.setVisibility(View.VISIBLE);
        binding.contador.setVisibility(View.VISIBLE);
        binding.contador.setText("0/160"); // Reinicia el contador
        binding.mensaje.setText(""); // Limpia el campo de mensaje
    }





//NOTAS: Yo estoy teniendo en cuenta los dos telefonos del contacto porque realmente aunque sean los mismos contactos,
    //se envian a numeros diferentes cuando se envia el sms al final.
    private void filtrarContactos() {
        listaContactos.clear();
        //Los espacios no se tendran en cuenta como parte del nombre por si
        // el usuario los introduce al final sin querer (lo mismo aplica al apellido)
        String nombreFiltro = binding.edtxtNombre.getText().toString().trim();
        String apellidoFiltro = binding.edtxtApellido.getText().toString().trim();

        // Reemplazar comodines para usarlos en la consulta
        String nombreQuery = nombreFiltro.replace("*", "%").replace("?", "_");
        String apellidoQuery = apellidoFiltro.replace("*", "%").replace("?", "_");

        ContentResolver contentResolver = getContentResolver();
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?";
        String[] selectionArgs ={nombreQuery + "% "+apellidoQuery + "%"};
       
        //aqui configuramos el cursos
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
            //si el cursor no lanza ningun contacto entonces lo que pasa es que no hubo coincidencias
            if(cursor.getCount()==0){
                Toast.makeText(this, "No se encontraron coincidencias", Toast.LENGTH_SHORT).show();

            }else {
                while (cursor.moveToNext()) {
                    String nombreCompleto = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String numero = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    Bitmap foto = null;
                    String fotoUri = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Photo.PHOTO_URI));
                    if (fotoUri != null) {
                        try {
                            InputStream inputStream = contentResolver.openInputStream(Uri.parse(fotoUri));
                            foto = BitmapFactory.decodeStream(inputStream);
                        } catch (Exception e) {
                            foto = null;
                        }
                    }
                    //añadimos el contacto a la lista
                    listaContactos.add(new Contacto(nombreCompleto, foto, numero));
                }
            }
            cursor.close();
        }

        adapter.notifyDataSetChanged();
    }



    private void mostrarDialogoConfirmacion() {
        //mostramos un dialogo cuando se le da a enviar
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialogo, null);
        builder.setView(dialogView);

        ImageView ivFoto = dialogView.findViewById(R.id.ivFoto);
        TextView tvNombre = dialogView.findViewById(R.id.tvNombre);
        TextView tvNumero = dialogView.findViewById(R.id.tvNumero);
        TextView tvMensaje = dialogView.findViewById(R.id.tvMensaje);

        tvNombre.setText("SMS enviado a: "+contactoSeleccionado.getNombre());
        tvNumero.setText(contactoSeleccionado.getNumero());
        tvMensaje.setText(binding.mensaje.getText().toString());

        if (contactoSeleccionado.getFoto() != null) {
            ivFoto.setImageBitmap(contactoSeleccionado.getFoto());
        } else {
            ivFoto.setImageResource(R.drawable.baseline_account_circle_24); // Imagen predeterminada
        }

        // Crear el cuadro de diálogo
        AlertDialog dialog = builder.create();

        // Configurar el botón para volver a la pag anterior
        Button volver = dialogView.findViewById(R.id.btnVolver);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Cierra el dialogo
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }



}