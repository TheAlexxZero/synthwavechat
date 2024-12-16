package com.example.synthwavechat;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmailRegister, etPasswordRegister, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginLink;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // inicializacion de las vistas
        etName = findViewById(R.id.etName);
        etEmailRegister = findViewById(R.id.etEmailRegister);
        etPasswordRegister = findViewById(R.id.etPasswordRegister);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        // inicializacion del firebaseauth
        mAuth = FirebaseAuth.getInstance();

        // evento para el boton de registro
        btnRegister.setOnClickListener(view -> registerUser());

        //evento para redirigir al login
        tvLoginLink.setOnClickListener(view -> finish());

    }

    private void registerUser()
    {
        String name = etName.getText().toString().trim();
        String email = etEmailRegister.getText().toString().trim();
        String password = etPasswordRegister.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        //validacion de los campos
        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Por favor ingrese su nombre completo", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "por favor ingrese su correo valido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Por favor ingrese una contraseña valida", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(confirmPassword))
        {
            Toast.makeText(this, "Las credenciales no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear cuenta con Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful())
                    {
                        // Registro exitoso
                        Toast.makeText(RegisterActivity.this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
                        // Aquí podrías guardar el nombre en la base de datos de Firebase o en Firestore
                        // FirebaseUser user = mAuth.getCurrentUser();
                        // Otras acciones después de crear la cuenta

                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this, "error al crear usuario", Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
