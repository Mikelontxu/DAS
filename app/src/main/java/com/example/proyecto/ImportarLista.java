package com.example.proyecto;

import android.content.Intent;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.nav_descargar_lista) {
            Intent intent = new Intent(this, DescargarLista.class);
            startActivity(intent);
        } else if (id == R.id.nav_importar_lista) {
            // Current activity
        } else if (id == R.id.nav_info) {
            Intent intent = new Intent(this, Info.class);
            startActivity(intent);
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
                importTxtFile(uri);
            }
        }
    }

    private void importTxtFile(Uri uri) {
        executorService.execute(() -> {
            InputStream inputStream = null;
            BufferedReader reader = null;
            try {
                inputStream = getContentResolver().openInputStream(uri);
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                Song song = null;
                AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                boolean isValidFormat = true;

                while ((line = reader.readLine()) != null) {
                    Log.d(TAG, "Reading line: " + line);
                    if (!line.trim().isEmpty()) {
                        String[] parts = line.split(": ");
                        if (parts.length == 2) {
                            String key = parts[0].trim();
                            String value = parts[1].trim();
                            Log.d(TAG, "Key: " + key + ", Value: " + value);
                            if (value.isEmpty()) {
                                value = null;
                            }
                            if (song == null) {
                                song = new Song();
                            }
                            switch (key) {
                                case "Título":
                                    song.setTitulo(value);
                                    break;
                                case "Artista":
                                    song.setArtista(value);
                                    break;
                                case "Álbum":
                                    song.setAlbum(value);
                                    break;
                                case "Fecha":
                                    song.setFecha(value);
                                    break;
                                case "Duración":
                                    song.setDuracion(value);
                                    break;
                                case "Género":
                                    song.setGenero(value);
                                    break;
                                default:
                                    isValidFormat = false;
                                    break;
                            }
                        } else {
                            isValidFormat = false;
                            break;
                        }
                    } else if (song != null) {
                        db.songDao().insertSong(song);
                        song = null;
                    }
                }

                if (song != null && isValidFormat) {
                    db.songDao().insertSong(song);
                }

                if (isValidFormat) {
                    runOnUiThread(() -> Toast.makeText(ImportarLista.this, "Subida de datos exitosa", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(ImportarLista.this, "Formato de archivo no válido", Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e) {
                Log.e(TAG, "Error reading file", e);
                runOnUiThread(() -> Toast.makeText(ImportarLista.this, "Error al importar el archivo", Toast.LENGTH_SHORT).show());
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error closing streams", e);
                }
            }
        });
    }
}