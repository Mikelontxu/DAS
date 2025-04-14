package com.example.proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import database.AppDatabase;
import database.User;

public class IniciarSesion extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button registerRedirectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlogin);

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        registerRedirectButton = findViewById(R.id.register_redirect_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(IniciarSesion.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Ejecutar la consulta en un hilo de fondo
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                    User user = db.userDao().getUserByUsername(username);

                    runOnUiThread(() -> {
                        if (user != null && user.getPassword().equals(password)) {
                            Toast.makeText(IniciarSesion.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                            // Guardar el ID del usuario en SharedPreferences
                            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("userId", user.getId());
                            editor.apply();
                            // Iniciar la actividad principal
                            Intent intent = new Intent(IniciarSesion.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showInvalidCredentialsDialog();
                        }
                    });
                });
            }
        });

        registerRedirectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IniciarSesion.this, Registrarse.class);
                startActivity(intent);
            }
        });
    }
    private void showInvalidCredentialsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Error de inicio de sesión")
                .setMessage("El usuario y la contraseña no coinciden. Por favor, inténtalo de nuevo.")
                .setPositiveButton("Aceptar", null)
                .show();
    }
}