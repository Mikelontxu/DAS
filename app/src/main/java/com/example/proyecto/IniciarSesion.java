package com.example.proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import api.UserApi;

public class IniciarSesion extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button registerRedirectButton;
    private ExecutorService executorService = Executors.newSingleThreadExecutor(); // Define ExecutorService

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlogin);

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);

        registerRedirectButton = findViewById(R.id.register_redirect_button);

        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(IniciarSesion.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            executorService.execute(() -> {
                try {
                    int userId = UserApi.loginUser(username, password); // Obtiene el user_id del servidor
                    runOnUiThread(() -> {
                        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("userId", userId); // Almacena el user_id en SharedPreferences
                        editor.apply();

                        // Redirige a MainActivity
                        Intent intent = new Intent(IniciarSesion.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(IniciarSesion.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });
        });

        registerRedirectButton.setOnClickListener(v -> {
            Intent intent = new Intent(IniciarSesion.this, Registrarse.class);
            startActivity(intent);
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