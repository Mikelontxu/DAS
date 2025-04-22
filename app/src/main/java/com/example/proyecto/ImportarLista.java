package com.example.proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import api.SongApi;
import database.AppDatabase;
import database.Song;
import utils.TemasUtils;

public class ImportarLista extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PICK_TXT_FILE = 1;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final String TAG = "ImportarLista";
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importar_lista);
        TemasUtils.applyTheme(this, findViewById(R.id.drawer_layout));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        Button btnImportarLista = findViewById(R.id.btn_importar_lista);
        btnImportarLista.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("text/plain");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, PICK_TXT_FILE);
        });
        // Handle back button press using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ImportarLista.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_mapa) {
            Intent intent = new Intent(this, Mapa.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_descargar_lista) {
            Intent intent = new Intent(this, DescargarLista.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_importar_lista) {
            // Actividad actual
        } else if (id == R.id.nav_logout) {
            // Cerrar sesión
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("userId"); // Borra el ID del usuario
            editor.apply();

            // Redirige a la pantalla de inicio de sesión
            Intent intent = new Intent(this, IniciarSesion.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_info) {
            Intent intent = new Intent(this, Info.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_TXT_FILE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                importJsonFile(uri);
            }
        }
    }

    private void importJsonFile(Uri uri) {
        executorService.execute(() -> {
            InputStream inputStream = null;
            BufferedReader reader = null;
            try {
                inputStream = getContentResolver().openInputStream(uri);
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                Song song = new Song();
                int userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("userId", -1);

                if (userId == -1) {
                    runOnUiThread(() -> Toast.makeText(this, "Usuario no identificado", Toast.LENGTH_SHORT).show());
                    return;
                }

                song.setUserId(userId);
                int lineCounter = 0;

                while ((line = reader.readLine()) != null) {
                    line = line.trim(); // Elimina espacios en blanco
                    if (line.isEmpty()) {
                        // Si encontramos una línea vacía, procesamos la canción actual
                        if (song.getTitulo() != null && song.getArtista() != null) {
                            Song finalSong = song;
                            executorService.execute(() -> {
                                try {
                                    boolean success = SongApi.addSong(finalSong);
                                    if (!success) {
                                        Log.e(TAG, "Error al agregar la canción al servidor: " + finalSong.getTitulo());
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error al agregar la canción: " + e.getMessage(), e);
                                }
                            });
                        }
                        // Reinicia el objeto `Song` para la siguiente canción
                        song = new Song();
                        song.setUserId(userId);
                        lineCounter = 0;
                    } else {
                        // Asigna los valores a los campos de la canción según el orden de las líneas
                        switch (lineCounter) {
                            case 0:
                                song.setTitulo(line);
                                break;
                            case 1:
                                song.setArtista(line);
                                break;
                            case 2:
                                song.setAlbum(line.isEmpty() ? null : line);
                                break;
                            case 3:
                                song.setFecha(line.isEmpty() ? null : line);
                                break;
                            case 4:
                                song.setDuracion(line.isEmpty() ? null : line);
                                break;
                            case 5:
                                song.setGenero(line.isEmpty() ? null : line);
                                break;
                            default:
                                Log.w(TAG, "Línea inesperada: " + line);
                                break;
                        }
                        lineCounter++;
                    }
                }

                // Procesa la última canción si no termina con una línea vacía
                if (song.getTitulo() != null && song.getArtista() != null) {
                    Song finalSong = song;
                    executorService.execute(() -> {
                        try {
                            boolean success = SongApi.addSong(finalSong);
                            if (!success) {
                                Log.e(TAG, "Error al agregar la canción al servidor: " + finalSong.getTitulo());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error al agregar la canción: " + e.getMessage(), e);
                        }
                    });
                }

                runOnUiThread(() -> Toast.makeText(this, "Importación completada", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                Log.e(TAG, "Error al importar el archivo: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(this, "Error al importar el archivo", Toast.LENGTH_SHORT).show());
            } finally {
                try {
                    if (reader != null) reader.close();
                    if (inputStream != null) inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error al cerrar el archivo: " + e.getMessage(), e);
                }
            }
        });
    }
}