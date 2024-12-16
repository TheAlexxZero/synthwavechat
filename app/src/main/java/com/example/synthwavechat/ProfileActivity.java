package com.example.synthwavechat;

import android.app.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends Activity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "ProfileActivity";

    private ImageView ivProfilePicture;
    private EditText etNickname;
    private Button btnChangePicture, btnSaveProfile;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // Enlaces a los elementos del layout
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        etNickname = findViewById(R.id.etNickname);
        btnChangePicture = findViewById(R.id.btnChangePicture);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        // Cargar imagen de perfil y nickname existentes
        loadUserProfile();

        // Cambiar imagen de perfil al hacer clic
        btnChangePicture.setOnClickListener(v -> openGallery());

        // Guardar cambios en el perfil
        btnSaveProfile.setOnClickListener(v -> saveProfileChanges());
    }

    private void loadUserProfile() {
        // Aquí se carga la imagen y el nickname desde Firebase (si ya se guardaron previamente)
        String currentUserId = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(currentUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Obtener datos de Firebase
                String nickname = task.getResult().child("nickname").getValue(String.class);
                String profilePictureUrl = task.getResult().child("profilePictureUrl").getValue(String.class);

                // Mostrar datos en la interfaz
                if (nickname != null) {
                    etNickname.setText(nickname);
                }
                if (profilePictureUrl != null) {
                    Picasso.get().load(profilePictureUrl).into(ivProfilePicture);
                }
            } else {
                Log.e(TAG, "Error al cargar los datos del usuario", task.getException());
            }
        });
    }

    private void openGallery() {
        // Abrir la galería para seleccionar una imagen
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Si la imagen fue seleccionada
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ivProfilePicture.setImageURI(imageUri);
        }
    }

    private void saveProfileChanges() {
        String nickname = etNickname.getText().toString().trim();

        if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(this, "Por favor, ingresa un nickname", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = mAuth.getCurrentUser().getUid();

        // Si se seleccionó una nueva imagen de perfil
        if (imageUri != null) {
            // Subir la nueva imagen al almacenamiento de Firebase
            StorageReference fileReference = mStorageRef.child("profile_pictures").child(currentUserId + ".jpg");
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String profilePictureUrl = uri.toString();

                            // Actualizar los datos en la base de datos de Firebase
                            mDatabase.child("users").child(currentUserId)
                                    .child("nickname").setValue(nickname);
                            mDatabase.child("users").child(currentUserId)
                                    .child("profilePictureUrl").setValue(profilePictureUrl);

                            Toast.makeText(ProfileActivity.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();

                            // Volver a la actividad de chat
                            finish();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error al subir la imagen", e);
                        Toast.makeText(ProfileActivity.this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Si no se seleccionó una nueva imagen, solo actualizar el nickname
            mDatabase.child("users").child(currentUserId)
                    .child("nickname").setValue(nickname);

            Toast.makeText(ProfileActivity.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
