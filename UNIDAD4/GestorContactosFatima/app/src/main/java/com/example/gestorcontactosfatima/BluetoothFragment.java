package com.example.gestorcontactosfatima;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.gestorcontactosfatima.databinding.FragmentBluetoothBinding;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BluetoothFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BluetoothFragment extends Fragment {

    private BluetoothAdapter bluetoothAdapter;
    private List<String> devicesList;
    private BluetoothDeviceAdapter deviceAdapter;
    private OnDeviceSelectedListener deviceSelectedListener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;





    // Interfaz para comunicar la opcion seleccionada al fragmento principal
    public interface OnDeviceSelectedListener {
        void onDeviceSelected(String deviceName);
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     */
    // TODO: Rename and change types and number of parameters


    public BluetoothFragment() {
        // Constructor vacío
    }

    public static BluetoothFragment newInstance() {
        return new BluetoothFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {


        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            if (context instanceof OnDeviceSelectedListener) {
                deviceSelectedListener = (OnDeviceSelectedListener) context;
            } else {
                throw new ClassCastException(context.toString() + " must implement OnDeviceSelectedListener");
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
            deviceSelectedListener = null; // Maneja la excepción si no se implementa
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentBluetoothBinding binding = FragmentBluetoothBinding.inflate(inflater, container, false);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        devicesList = new ArrayList<>();

        // Configurar el RecyclerView
        binding.devicesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        deviceAdapter = new BluetoothDeviceAdapter(devicesList, device -> {
            String selectedDevice = device.split("\n")[0];
            // Crear un Bundle para pasar los datos de un fragmento a otro
            Bundle bundle = new Bundle();
            bundle.putString("selectedDevice", selectedDevice);

            // Crear el ContactListFragment
            ContactListFragment contactListFragment = new ContactListFragment();
            contactListFragment.setArguments(bundle);

            // Reemplazar el fragmento actual por ContactListFragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, contactListFragment)
                    .addToBackStack(null)
                    .commit();

        });
        binding.devicesRecyclerView.setAdapter(deviceAdapter);

        if (bluetoothAdapter == null) {
            Toast.makeText(requireContext(), "Bluetooth no está disponible", Toast.LENGTH_SHORT).show();
            return binding.getRoot();
        }

        // Verificar permisos antes de continuar
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
            mostrarDispositivosEmparejados();
        } else {
            Toast.makeText(requireContext(), "Permiso de Bluetooth denegado", Toast.LENGTH_SHORT).show();
        }

        return binding.getRoot();
    }

    private void mostrarDispositivosEmparejados() {

        devicesList.clear();
        //No voy a aañadir la opcion de poder manejar dispositivos emparejados reales, solo simulados

            agregarDispositivosSimulados();

        deviceAdapter.notifyDataSetChanged();
    }

    private void agregarDispositivosSimulados() {
        List<BluetoothDeviceSimulated> simulatedDevices = new ArrayList<>();
        simulatedDevices.add(new BluetoothDeviceSimulated("Dispositivo 1", "00:11:22:33:44:55"));
        simulatedDevices.add(new BluetoothDeviceSimulated("Dispositivo 2", "66:77:88:99:AA:BB"));

        for (BluetoothDeviceSimulated device : simulatedDevices) {
            devicesList.add(device.getNombre() + "\n" + device.getDireccion());
        }
    }
}