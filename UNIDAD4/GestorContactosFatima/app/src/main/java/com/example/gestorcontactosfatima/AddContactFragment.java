package com.example.gestorcontactosfatima;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.Manifest;

import com.example.gestorcontactosfatima.databinding.FragmentAddContactBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddContactFragment extends Fragment {
    private FragmentAddContactBinding binding;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddContactFragment newInstance(String param1, String param2) {
        AddContactFragment fragment = new AddContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddContactBinding.inflate(inflater, container, false);

        // Verificar permisos al iniciar porque sino peta el programa al querer añadir un contacto
        //sin permisos concedidos anteriormente

        verificarPermisos();

        // boton de guardar contacto
        binding.buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!verificarPermisos()) {
                    Toast.makeText(requireContext(), "Permisos no concedidos", Toast.LENGTH_SHORT).show();
                    return;
                }
                String nombre=binding.editTextNom.getText().toString();
                String numero=binding.editTextP.getText().toString();

                //validamos todos los campos y no dejaremos que se introduzcan un nombre sin apellido o algun campo vacio
                //o un numero que no siga un formato especifico

                if (nombre.isEmpty() || numero.isEmpty()) {
                    Toast.makeText(requireContext(), "Por favor, completa ambos campos", Toast.LENGTH_SHORT).show();
                } else if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+\\s+[a-zA-ZáéíóúÁÉÍÓÚñÑ]+$")) {
                    Toast.makeText(requireContext(), "El nombre debe contener al menos un espacio que separe el nombre y el primer apellido y estar compuesto por solo letras", Toast.LENGTH_SHORT).show();
                } else if (!numero.matches("^\\d{1}-\\d{3}-\\d{3}-\\d{3}$|^\\d{3}-\\d{3}-\\d{4}$")) {
                    Toast.makeText(requireContext(), "El número debe estar en formato 1-111-111-111 o 111-111-1111", Toast.LENGTH_SHORT).show();
                } else {
                    //si pasa todas las validaciones, agregamos contacto
                    agregarContacto(nombre, numero);
                }


                binding.editTextNom.setText("");
                binding.editTextP.setText("");

            }
        });

        return binding.getRoot();
    }

    private boolean verificarPermisos() {
        boolean permisosConcedidos = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;

       //Si los permisos estan concedidos el boton se habilita, si no estan concedidos el boton se desabilita para evitar que el
        //programa se rompa
        binding.buttonGuardar.setEnabled(permisosConcedidos);

        return permisosConcedidos;
    }

    private void agregarContacto(String nombre, String numero) {
        ContentResolver contentResolver = requireContext().getContentResolver();

        ContentValues values = new ContentValues();
        values.put(ContactsContract.RawContacts.ACCOUNT_TYPE, (String) null);
        values.put(ContactsContract.RawContacts.ACCOUNT_NAME, (String) null);
        Uri rawContactUri = contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri); //obtenemos el id del contacto creado

        // Nombre
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nombre);
        contentResolver.insert(ContactsContract.Data.CONTENT_URI, values);

        // Teléfono
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, numero);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        contentResolver.insert(ContactsContract.Data.CONTENT_URI, values);
        //notiificamos al usuario de que el contacto se ha agregado correctamente
        Toast.makeText(requireContext(), "Contacto agregado correctamente", Toast.LENGTH_SHORT).show();



    }


}