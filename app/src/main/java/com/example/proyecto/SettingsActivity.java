package com.example.proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.google.android.material.navigation.NavigationView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import database.AppDatabase;
import utils.TemasUtils;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = prefs.getString("theme", "white");
        setContentView(R.layout.activity_settings);
        View rootView = findViewById(R.id.drawer_layout);
        TemasUtils.applyTheme(this, rootView);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the drawer layout
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();

        // Register the preference change listener
        preferenceChangeListener = (sharedPreferences, key) -> {
            if ("theme".equals(key)) {
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        // Handle back button press using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.nav_descargar_lista) {
            Intent intent = new Intent(this, DescargarLista.class);
            startActivity(intent);
        } else if (id == R.id.nav_importar_lista) {
            Intent intent = new Intent(this, ImportarLista.class);
            startActivity(intent);
        } else if (id == R.id.nav_info) {
            // Handle Info action
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private ExecutorService executorService = Executors.newSingleThreadExecutor();

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            Preference deleteAllSongsPreference = findPreference("delete_all_songs");
            if (deleteAllSongsPreference != null) {
                deleteAllSongsPreference.setOnPreferenceClickListener(preference -> {
                    showConfirmationDialog();
                    return true;
                });
            }
        }

        private void showConfirmationDialog() {
            new AlertDialog.Builder(getContext())
                    .setTitle("Confirmar borrado")
                    .setMessage("¿Estás seguro de que quieres borrar todos los datos de la base de datos?")
                    .setPositiveButton("Sí", (dialog, which) -> deleteAllSongs())
                    .setNegativeButton("No", null)
                    .show();
        }
        private void deleteAllSongs() {
            executorService.execute(() -> {
                AppDatabase db = AppDatabase.getDatabase(getContext());
                db.songDao().deleteAllSongs();
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Canciones borradas", Toast.LENGTH_SHORT).show());
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}