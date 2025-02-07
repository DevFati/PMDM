package com.example.gestorcontactosfatima;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.gestorcontactosfatima.databinding.FragmentContactListBinding;
import android.Manifest;
import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 */
public class ContactListFragment extends Fragment {
    private FragmentContactListBinding binding;
    private static ContactAdapter adapter;
    private static ArrayList<Contact> listaContactos=new ArrayList<>();
    private static Contact contactoSeleccionado;
    private static final int SOLICITUD_PERMISO_ESCRIBIR_CONTACTOS = 101;



    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ContactListFragment newInstance(int columnCount) {
        ContactListFragment fragment = new ContactListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentContactListBinding.inflate(inflater, container, false);
        View view=binding.getRoot();

        // Verificar si hay datos en el Bundle
        Bundle args = getArguments();
        if (args != null && args.containsKey("selectedDevice")) {
            String dispositivoSeleccionado = args.getString("selectedDevice");
            Toast.makeText(requireContext(), "Conexion exitosa  a " + dispositivoSeleccionado, Toast.LENGTH_SHORT).show();
        }

        //Metemos en la lista todos los contactos del telefono
        listaContactos=contactosTelefono();
        if(listaContactos.size()==0){
            Toast.makeText(requireContext(), "No hay contactos", Toast.LENGTH_SHORT).show();

        }
        adapter = new ContactAdapter(getContext(), listaContactos);
        binding.contactoListView.setAdapter(adapter);

        //detectamos cuando el usuario hace click en un contacto
        binding.contactoListView.setOnItemClickListener((parent,view1,position,id) ->{

            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                contactoSeleccionado = listaContactos.get(position); // Contacto seleccionado

                // Mostrar el cuadro de diálogo para editar
                mostrarDialogoEdicion(contactoSeleccionado, position);
            } else {
                // Solicitar el permiso
                Toast.makeText(requireContext(), "Permiso de escritura necesario", Toast.LENGTH_SHORT).show();

                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.WRITE_CONTACTS},
                        SOLICITUD_PERMISO_ESCRIBIR_CONTACTOS);
            }
        });

        // Detectamos cuando el usuario hace un long click en un contacto
        binding.contactoListView.setOnItemLongClickListener((parent, view1, position, id) -> {
            if (args != null && args.containsKey("selectedDevice")) {
                String dispositivo = args.getString("selectedDevice");
                Contact contacto = listaContactos.get(position);
                enviarVCard(contacto, dispositivo);
            } else {
                Toast.makeText(requireContext(), "No hay dispositivo Bluetooth conectado", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        return view;
    }


    private void enviarVCard(Contact contacto, String dispositivo) {
        String vCard = "BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "FN:" + contacto.getNombre() + "\n" +
                "TEL:" + contacto.getTelefono() + "\n" +
                "END:VCARD";

        // Crear el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enviar Contacto")
                .setMessage("Contacto enviado al dispositivo " + dispositivo + ":\n\n" + vCard)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    // Cierra el diálogo
                    dialog.dismiss();
                });

        // Mostrar el diálogo
        builder.create().show();
    }



    // Método para mostrar el cuadro de diálogo para editar un contacto
    private void mostrarDialogoEdicion(Contact contacto, int position) {
        // Crear un View para el diálogo
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.fragment_contact_details, null);

        // Referencias a los campos del diálogo
        EditText editN = dialogView.findViewById(R.id.nom);
        EditText editP = dialogView.findViewById(R.id.tel);

        // Cargar datos actuales en los campos
        editN.setText(contacto.getNombre());
        editP.setText(contacto.getTelefono());

        // Crear el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Editar Contacto")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    // Obtener los nuevos datos
                    String nuevoN = editN.getText().toString();
                    String nuevoT = editP.getText().toString();

                    // Validamos que los dos campos esten rellenos y que el telefono siga un formato

                    if (nuevoN.isEmpty() || nuevoT.isEmpty()) {
                        Toast.makeText(requireContext(), "Por favor, completa ambos campos", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!nuevoN.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+\\s+[a-zA-ZáéíóúÁÉÍÓÚñÑ]+$")) {
                        Toast.makeText(requireContext(), "El nombre debe contener al menos un espacio que separe el nombre y el primer apellido y estar compuesto por solo letras", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!nuevoT.matches("^\\d{1}-\\d{3}-\\d{3}-\\d{3}$|^\\d{3}-\\d{3}-\\d{4}$")) {
                        Toast.makeText(requireContext(), "El número debe estar en formato 1-111-111-111 o 111-111-1111", Toast.LENGTH_SHORT).show();
                        return;
                    }





                    // Actualizar el contacto
                    actualizarContacto(nuevoN, nuevoT, contactoSeleccionado.getNombre(), contactoSeleccionado.getTelefono());

                    // Recargar la lista de contactos con los datos actualizados
                    listaContactos.clear();
                    listaContactos.addAll(contactosTelefono());
                    adapter.notifyDataSetChanged();

                    Toast.makeText(requireContext(), "Contacto actualizado correctamente", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss(); // Cerrar el diálogo sin hacer cambios
                })
                .create()
                .show();
    }




    public void actualizarContacto(String nuevoNombre, String nuevoTelefono, String nombreOriginal, String telefonoOriginal) {
        ContentResolver resolver = requireContext().getContentResolver();

        // Buscar el ID del contacto basado en el número original para ser mas precisos
        String[] projection = {ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone._ID};
        String where = ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?";
        String[] args = new String[]{telefonoOriginal};

        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, where, args, null);

        if (cursor != null && cursor.moveToFirst()) {
            String phoneId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID));
            String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            cursor.close();

            // Actualizar el número de teléfono (sin tocar el nombre)
            if (!telefonoOriginal.equals(nuevoTelefono)) {
                ContentValues valoresTelefono = new ContentValues();
                valoresTelefono.put(ContactsContract.CommonDataKinds.Phone.NUMBER, nuevoTelefono);

                resolver.update(
                        ContactsContract.Data.CONTENT_URI,
                        valoresTelefono,
                        ContactsContract.Data._ID + " = ? AND " +
                                ContactsContract.Data.MIMETYPE + " = ?",
                        new String[]{phoneId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE}
                );
            }

            // Actualizar el nombre para todos los números del contacto solo si es necesario
            if (!nombreOriginal.equals(nuevoNombre)) {
                ContentValues valoresNombre = new ContentValues();
                valoresNombre.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nuevoNombre);

                resolver.update(
                        ContactsContract.Data.CONTENT_URI,
                        valoresNombre,
                        ContactsContract.Data.CONTACT_ID + " = ? AND " +
                                ContactsContract.Data.MIMETYPE + " = ?",
                        new String[]{contactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE}
                );
            }

            Toast.makeText(requireContext(), "Contacto actualizado correctamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "No se encontró el contacto", Toast.LENGTH_SHORT).show();
        }
    }






    private ArrayList<Contact> contactosTelefono() {
        ArrayList<Contact> contactos = new ArrayList<>();
        ContentResolver resolver = requireContext().getContentResolver();

        Cursor cursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String telefono = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));



                // Evitamos duplicados
                Contact contacto = new Contact(nombre, telefono);
                if (!contactos.contains(contacto)) {
                    contactos.add(contacto);
                }

            }
            cursor.close();
        }

        return contactos;
    }



    //recargo la lista de contactos xcada vez que se hace visible para que cuando agreguemos un contacto lo podamos ver en la lista
    //y editarlo si queremos
    @Override
    public void onResume() {
        super.onResume();
        // Actualizar la lista de contactos
        listaContactos.clear();
        listaContactos.addAll(contactosTelefono());
        adapter.notifyDataSetChanged();
    }




}