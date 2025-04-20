package com.example.proyecto;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import api.SongApi;
import database.AppDatabase;
import database.Song;
import utils.TemasUtils;

// Imports para el calendario
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

public class crearCancion extends AppCompatActivity {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_cancion);
        TemasUtils.applyTheme(this, findViewById(R.id.root_layout));

        EditText titulo = findViewById(R.id.titulo);
        EditText artista = findViewById(R.id.artista);
        EditText album = findViewById(R.id.album);
        EditText fecha = findViewById(R.id.fecha);
        EditText duracion = findViewById(R.id.duracion);
        EditText genero = findViewById(R.id.genero);
        Button btnGuardar = findViewById(R.id.btn_guardar);

        fecha.setOnClickListener(v -> {
            // Obtén la fecha actual
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Crea un DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(crearCancion.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Formatea la fecha como YYYY-MM-DD
                        String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        fecha.setText(selectedDate);
                    }, year, month, day);

            // Muestra el DatePickerDialog
            datePickerDialog.show();
        });

        btnGuardar.setOnClickListener(v -> {
            String tituloText = titulo.getText().toString();
            String artistaText = artista.getText().toString();
            String fechaText = fecha.getText().toString();
            String duracionText = duracion.getText().toString();

            if (tituloText.isEmpty() || artistaText.isEmpty()) {
                new AlertDialog.Builder(crearCancion.this)
                        .setTitle("Campos obligatorios")
                        .setMessage("Por favor, introduce el título y el artista de la canción.")
                        .setPositiveButton("OK", null)
                        .show();
            } else if (!fechaText.matches("\\d{4}-\\d{2}-\\d{2}")) {
                // Validar formato de fecha
                new AlertDialog.Builder(crearCancion.this)
                        .setTitle("Formato de fecha incorrecto")
                        .setMessage("La fecha debe estar en el formato YYYY-MM-DD.")
                        .setPositiveButton("OK", null)
                        .show();
            } else if (!duracionText.matches("\\d{1,2}:\\d{2}")) {
                // Validar formato de duración
                new AlertDialog.Builder(crearCancion.this)
                        .setTitle("Formato de duración incorrecto")
                        .setMessage("La duración debe estar en el formato H:MM.")
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                Song song = new Song();
                song.setTitulo(tituloText);
                song.setArtista(artistaText);
                song.setAlbum(album.getText().toString());
                song.setFecha(fechaText);
                song.setDuracion(duracionText);
                song.setGenero(genero.getText().toString());

                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                int userId = prefs.getInt("userId", -1);
                if (userId != -1) {
                    song.setUserId(userId);
                    executorService.execute(() -> {
                        try {
                            boolean success = SongApi.addSong(song);
                            if (success) {
                                runOnUiThread(() -> {
                                    setResult(RESULT_OK);
                                    finish();
                                });
                            }
                        } catch (Exception e) {
                            runOnUiThread(() -> Toast.makeText(crearCancion.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    });
                }
            }
        });
    }
    public void volverAtras(View view) {
        finish();
    }
}