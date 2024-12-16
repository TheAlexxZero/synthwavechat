package com.example.synthwavechat;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainMenuActivity extends AppCompatActivity {

    private ImageView ivBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottomnavigationview);

        // Cargar el fondo GIF
        ivBackground = findViewById(R.id.ivBackground);
        Glide.with(this)
                .asGif()
                .load(R.raw.synthwave_2) // Cambia a tu archivo de GIF en res/drawable
                .into(ivBackground);

        // Configurar BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            // Compara los títulos del ítem para determinar cuál se seleccionó
            switch (item.getTitle().toString()) {
                case "Chats":
                    selectedFragment = new ChatsFragment();
                    break;
                case "Contactos":
                    selectedFragment = new ContactsFragment();
                    break;
                case "Perfil":
                    selectedFragment = new ProfileFragment();
                    break;
            }

            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            }
            return true;
        });

        // Mostrar el fragmento inicial (Chats por defecto)
        if (savedInstanceState == null) {
            replaceFragment(new ChatsFragment());
        }
    }

    // Método para reemplazar los fragmentos
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContent, fragment)
                .commit();
    }
}
