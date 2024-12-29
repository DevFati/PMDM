package com.example.fatimagestorcontactos;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.fatimagestorcontactos.databinding.ContactoItemBinding;
import com.example.fatimagestorcontactos.placeholder.PlaceholderContent.PlaceholderItem;
import com.example.fatimagestorcontactos.databinding.FragmentContactListBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ContactAdapter extends ArrayAdapter<Contact> {


    public ContactAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact c = getItem(position);


        ContactoItemBinding binding;
        if (convertView == null) {
            binding = ContactoItemBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ContactoItemBinding) convertView.getTag();
        }


        binding.nombreC.setText(c.getNombre());
        binding.telefonoC.setText(c.getTelefono());

        return convertView;
    }
}