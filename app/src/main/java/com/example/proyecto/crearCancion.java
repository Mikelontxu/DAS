// AddSongActivity.java
package com.example.proyecto;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import database.AppDatabase;
import database.Song;

public class crearCancion extends AppCompatActivity {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_cancion);

        EditText titulo = findViewById(R.id.titulo);
        EditText artista = findViewById(R.id.artista);
        EditText album = findViewById(R.id.album);
        EditText fecha = findViewById(R.id.fecha);
        EditText duracion = findViewById(R.id.duracion);
        EditText genero = findViewById(R.id.genero);
        Button btnGuardar = findViewById(R.id.btn_guardar);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Song song = new Song();
                song.setTitulo(titulo.getText().toString());
                song.setArtista(artista.getText().toString());
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
        });
    }
}