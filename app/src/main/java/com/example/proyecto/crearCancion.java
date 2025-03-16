package com.example.proyecto;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import database.AppDatabase;
import database.Song;
import utils.TemasUtils;

// Imports para el calendario
import android.app.DatePickerDialog;
import android.widget.DatePicker;
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
                        // Actualiza el campo de texto con la fecha seleccionada
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        fecha.setText(selectedDate);
                    }, year, month, day);

            // Muestra el DatePickerDialog
            datePickerDialog.show();
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tituloText = titulo.getText().toString();
                String artistaText = artista.getText().toString();
                // Comprobamos que los campos obligatorios no estén vacíos y salta un dialogo si lo esta
                if (tituloText.isEmpty() || artistaText.isEmpty()) {
                    new AlertDialog.Builder(crearCancion.this)
                            .setTitle("Campos obligatorios")
                            .setMessage("Por favor, introduce el título y el artista de la canción.")
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    Song song = new Song();
                    song.setTitulo(tituloText);
                    song.setArtista(artistaText);
                    song.setAlbum(album.getText().toString());
                    song.setFecha(fecha.getText().toString());
                    song.setDuracion(duracion.getText().toString());
                    song.setGenero(genero.getText().toString());

                    executorService.execute(() -> {
                        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                        db.songDao().insertSong(song);
                        setResult(RESULT_OK); // Set result to OK
                        finish();
                    });
                }
            }
        });
    }
    public void volverAtras(View view) {
        finish();
    }
}