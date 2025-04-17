package com.example.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import database.AppDatabase;
import database.User;

public class Registrarse extends AppCompatActivity {

    private EditText usernameInput, emailInput, passwordInput, repeatPasswordInput;
    private Button registerButton;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        repeatPasswordInput = findViewById(R.id.repeat_password_input);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String repeatPassword = repeatPasswordInput.getText().toString().trim();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
                    Toast.makeText(Registrarse.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(repeatPassword)) {
                    Toast.makeText(Registrarse.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    return;
                }

                executorService.execute(() -> {
                    AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                    User existingUser = db.userDao().getUserByUsername(username);

                    if (existingUser != null) {
                        runOnUiThread(() -> Toast.makeText(Registrarse.this, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show());
                    } else {
                        User newUser = new User(username, password, email);
                        db.userDao().insertUser(newUser);
                        runOnUiThread(() -> {
                            Toast.makeText(Registrarse.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Registrarse.this, IniciarSesion.class);
                            startActivity(intent);
                            finish();
                        });
                    }
                });
            }
        });
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Finaliza la actividad actual y vuelve a la anterior
            }
        });
    }
}