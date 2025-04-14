package com.example.proyecto;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import database.AppDatabase;
import database.Song;
import adaptadores.SongAdapter;
import utils.TemasUtils;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private SongAdapter adapter;
    private List<Song> songList = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = prefs.getString("theme", "white");
        setContentView(R.layout.activity_main);
        View rootView = findViewById(R.id.drawer_layout);
        TemasUtils.applyTheme(this, rootView);

        // Set up the toolbar and navigation drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializa el adaptador con la lista de canciones
        adapter = new SongAdapter(songList);
        recyclerView.setAdapter(adapter);

        loadSongs();

        // Register the activity result launcher for detallesCancion
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Reload the song list
                        loadSongs();
                    }
                }
        );

        adapter.setOnItemClickListener(song -> {
            Intent intent = new Intent(MainActivity.this, detallesCancion.class); // Ensure this points to detallesCancion
            intent.putExtra("id", song.getId());
            intent.putExtra("titulo", song.getTitulo());
            intent.putExtra("artista", song.getArtista());
            intent.putExtra("album", song.getAlbum());
            intent.putExtra("fecha", song.getFecha());
            intent.putExtra("duracion", song.getDuracion());
            intent.putExtra("genero", song.getGenero());
            launcher.launch(intent);
        });
    }
    private void loadSongs() {
        executorService.execute(() -> {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            int userId = prefs.getInt("userId", -1); // Recuperar el ID del usuario actual

            if (userId != -1) {
                AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                songList = db.songDao().getSongsByUserId(userId); // Filtrar canciones por userId

                runOnUiThread(() -> {
                    adapter.updateSongs(songList); // Actualizar el adaptador con las canciones filtradas
                });
            }
        });
    }

    @Override
    @SuppressLint("MissingSuperCall")
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
        new AlertDialog.Builder(this)
                .setMessage("¿Estás seguro que quieres salir de la aplicación?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Actividad actual
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_descargar_lista) {
            Intent intent = new Intent(this, DescargarLista.class);
            startActivity(intent);
        } else if (id == R.id.nav_importar_lista) {
            Intent intent = new Intent(this, ImportarLista.class);
            startActivity(intent);
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
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add_song) {
            Intent intent = new Intent(this, crearCancion.class);
            startActivityForResult(intent, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadSongs(); // Reload the song list
        }
    }
}