package com.example.proyecto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.Manifest;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import alarma.AlarmHelper;
import api.SongApi;
import api.UserApi;
import database.AppDatabase;
import utils.TemasUtils;


public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
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

        // Update user profile section
        ImageView profilePicture = findViewById(R.id.profile_picture);
        TextView userName = findViewById(R.id.user_name);

        int userId = prefs.getInt("userId", -1);

        if (userId != -1) {
            executorService.execute(() -> {
                Bitmap profilePictureBitmap = null;
                String username = null;

                try {
                    // Fetch profile picture as Bitmap
                    profilePictureBitmap = UserApi.getProfilePicture(userId);
                } catch (Exception e) {
                    Log.e("SettingsActivity", "Error fetching profile picture: " + e.getMessage(), e);
                }

                try {
                    // Fetch username
                    username = UserApi.getUsername(userId);
                } catch (Exception e) {
                    Log.e("SettingsActivity", "Error fetching username: " + e.getMessage(), e);
                }

                Bitmap finalProfilePictureBitmap = profilePictureBitmap;
                String finalUsername = username;

                runOnUiThread(() -> {
                    if (finalUsername != null) {
                        userName.setText(finalUsername);
                    } else {
                        Toast.makeText(this, "Error loading username", Toast.LENGTH_SHORT).show();
                    }

                    if (finalProfilePictureBitmap != null) {
                        profilePicture.setImageBitmap(finalProfilePictureBitmap);
                    } else {
                        Toast.makeText(this, "Error loading profile picture", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
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
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_mapa) {
            Intent intent = new Intent(this, Mapa.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_settings) {
            // Already in Settings activity, do nothing
        } else if (id == R.id.nav_descargar_lista) {
            Intent intent = new Intent(this, DescargarLista.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_importar_lista) {
            Intent intent = new Intent(this, ImportarLista.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
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

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private ExecutorService executorService = Executors.newSingleThreadExecutor();
        private ActivityResultLauncher<Intent> cameraLauncher;
        private ActivityResultLauncher<Intent> galleryLauncher;
        private ActivityResultLauncher<String> requestPermissionLauncher;


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            // Initialize permission launcher
            requestPermissionLauncher = registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            Log.d("SettingsFragment", "Permiso de cámara concedido");
                            launchCameraIntent();
                        } else {
                            Log.d("SettingsFragment", "Permiso de cámara denegado");
                            Toast.makeText(getContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            Preference deleteAllSongsPreference = findPreference("delete_all_songs");
            if (deleteAllSongsPreference != null) {
                deleteAllSongsPreference.setOnPreferenceClickListener(preference -> {
                    showConfirmationDialog();
                    return true;
                });
            }

            initializeActivityResultLaunchers();
            Preference changeProfilePicturePreference = findPreference("change_profile_picture");
            if (changeProfilePicturePreference != null) {
                changeProfilePicturePreference.setOnPreferenceClickListener(preference -> {
                    showImagePickerDialog();
                    return true;
                });
            }


            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
            boolean isAlarmEnabled = sharedPreferences.getBoolean("enable_alarm", false);
            if (isAlarmEnabled) {
                AlarmHelper.scheduleDailyReminder(requireContext());
            }
            // Escuchar cambios en la preferencia de alarma
            Preference enableAlarmPreference = findPreference("enable_alarm");
            if (enableAlarmPreference != null) {
                enableAlarmPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean isEnabled = (boolean) newValue;
                    if (isEnabled) {
                        AlarmHelper.scheduleDailyReminder(requireContext());
                        Toast.makeText(getContext(), "Recordatorio activado", Toast.LENGTH_SHORT).show();
                    } else {
                        AlarmHelper.cancelDailyReminder(requireContext());
                        Toast.makeText(getContext(), "Recordatorio desactivado", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                });
            }
        }

        private void initializeActivityResultLaunchers() {
            cameraLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                            if (photo != null) {
                                uploadProfilePicture(photo);
                            } else {
                                Toast.makeText(getContext(), "Error al capturar la imagen", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            galleryLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri selectedImageUri = result.getData().getData();
                            try {
                                Bitmap photo = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImageUri);
                                uploadProfilePicture(photo);
                            } catch (IOException e) {
                                Toast.makeText(getContext(), "Error al procesar la imagen seleccionada", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );
        }

        private void showImagePickerDialog() {
            new AlertDialog.Builder(getContext())
                    .setTitle("Cambiar foto de perfil")
                    .setMessage("Selecciona una opción")
                    .setPositiveButton("Cámara", (dialog, which) -> openCamera())
                    .setNegativeButton("Galería", (dialog, which) -> openGallery())
                    .show();
        }

        private void openCamera() {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Log.d("SettingsFragment", "Solicitando permiso de cámara");
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            } else {
                Log.d("SettingsFragment", "Permiso de cámara ya concedido");
                launchCameraIntent();
            }
        }

        private void launchCameraIntent() {
            if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    Log.d("SettingsFragment", "Lanzando intent de cámara");
                    cameraLauncher.launch(cameraIntent);
                } else {
                    Log.d("SettingsFragment", "No se encontró una aplicación de cámara");
                    Toast.makeText(getContext(), "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("SettingsFragment", "El dispositivo no tiene cámara");
                Toast.makeText(getContext(), "El dispositivo no tiene cámara", Toast.LENGTH_SHORT).show();
            }
        }
        private void openGallery() {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(galleryIntent);
        }

        private void showConfirmationDialog() {
            new AlertDialog.Builder(getContext())
                    .setTitle("Confirmar borrado")
                    .setMessage("¿Estás seguro de que quieres borrar todos los datos de tu lista?")
                    .setPositiveButton("Sí", (dialog, which) -> deleteAllSongs())
                    .setNegativeButton("No", null)
                    .show();
        }

        private void deleteAllSongs() {
            SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            int userId = prefs.getInt("userId", -1);

            if (userId != -1) {
                executorService.execute(() -> {
                    try {
                        boolean success = SongApi.deleteAllSongs(userId);
                        getActivity().runOnUiThread(() -> {
                            if (success) {
                                Toast.makeText(getContext(), "Todas las canciones han sido borradas", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Error al borrar canciones en el servidor", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                });
            } else {
                Toast.makeText(getContext(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
            }
        }

        private void uploadProfilePicture(Bitmap photo) {
            SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            int userId = prefs.getInt("userId", -1);

            if (userId != -1) {
                executorService.execute(() -> {
                    try {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] fileData = byteArrayOutputStream.toByteArray();

                        boolean success = UserApi.uploadProfilePictureMultipart(userId, "profile.jpg", fileData);

                        getActivity().runOnUiThread(() -> {
                            if (success) {
                                Toast.makeText(getContext(), "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
                                // Reinicia la actividad para actualizar la interfaz
                                Intent intent = new Intent(getContext(), SettingsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "Error al subir la foto", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error al subir la foto", Toast.LENGTH_SHORT).show());
                    }
                });
            } else {
                Toast.makeText(getContext(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}