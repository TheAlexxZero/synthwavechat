package com.example.synthwavechat;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private ContactsAdapter contactsAdapter;
    private RecyclerView recyclerViewContacts;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        recyclerViewContacts = findViewById(R.id.recyclerViewContacts);
        searchView = findViewById(R.id.searchView);

        // Crear la lista de contactos (por ejemplo, cargados desde la base de datos)
        List<Contact> contactList = new ArrayList<>();
        // Agrega aquí los contactos, ejemplo:
        contactList.add(new Contact("Alicia", "alicia@example.com"));
        contactList.add(new Contact("Juan", "juan@example.com"));

        // Inicializar el adaptador
        contactsAdapter = new ContactsAdapter(contactList);
        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewContacts.setAdapter(contactsAdapter);

        // Configurar SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // No hacemos nada al presionar enter, solo filtramos en tiempo real
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Llamamos al filtro del adaptador
                contactsAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
