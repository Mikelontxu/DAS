package com.example.proyecto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import java.util.ArrayList;
import java.util.List;

import utils.TemasUtils;

public class Mapa extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_mapa);
        TemasUtils.applyTheme(this, findViewById(R.id.drawer_layout));

        // Configuración del mapa
        mapView = findViewById(R.id.map);
        mapView.setMultiTouchControls(true);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        requestLocationPermission();

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

        // Handle back button press using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(Mapa.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // Initialize the map
        mapView = findViewById(R.id.map);
        mapView.setMultiTouchControls(true);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);

        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        GeoPoint bilbao = new GeoPoint(43.2630126, -2.9349852); // Bilbao
        mapController.setCenter(bilbao);

        // Add markers for music stores
        addMusicStoreMarkers();
    }

    private void addMusicStoreMarkers() {
        List<GeoPoint> musicStores = new ArrayList<>();
        musicStores.add(new GeoPoint(43.2909943,-2.9920058));
        musicStores.add(new GeoPoint(43.2722425, -2.9438684));
        musicStores.add(new GeoPoint(43.2579858, -2.9255475));
        musicStores.add(new GeoPoint(43.2618413, -2.9257874));
        musicStores.add(new GeoPoint(43.2586317, -2.9350472));
        musicStores.add(new GeoPoint(43.2597015, -2.9310694));

        List<String> storeNames = new ArrayList<>();
        storeNames.add("Musivox - Baracaldo");
        storeNames.add("Musical Deusto");
        storeNames.add("Pentagrama Musika Denda");
        storeNames.add("Power Records");
        storeNames.add("Harmony Rock");
        storeNames.add("Jazz Cultural Store");

        for (int i = 0; i < musicStores.size(); i++) {
            Marker marker = new Marker(mapView);
            marker.setPosition(musicStores.get(i));
            marker.setTitle(storeNames.get(i)); // Asigna un nombre personalizado
            mapView.getOverlays().add(marker);
        }
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
            // Already in Mapa activity, do nothing
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent (this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            enableUserLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private FusedLocationProviderClient fusedLocationClient;

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            try {
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        GeoPoint userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                        addUserLocationMarker(userLocation);
                    } else {
                        Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (SecurityException e) {
                Toast.makeText(this, "Error de seguridad al acceder a la ubicación", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show();
        }
    }

    private void addUserLocationMarker(GeoPoint userLocation) {
        Marker userMarker = new Marker(mapView);
        userMarker.setPosition(userLocation);
        userMarker.setTitle("Tu ubicación");
        mapView.getOverlays().add(userMarker);

        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(userLocation);
    }
}
