package com.example.proyecto;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import database.AppDatabase;
import database.Song;
import utils.TemasUtils;

public class detallesCancion extends AppCompatActivity {

    private int id;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_cancion);
        TemasUtils.applyTheme(this, findViewById(R.id.root_layout));

        TextView titulo = findViewById(R.id.titulo);
        TextView artista = findViewById(R.id.artista);
        TextView album = findViewById(R.id.album);
        TextView fecha = findViewById(R.id.fecha);
        TextView duracion = findViewById(R.id.duracion);
        TextView genero = findViewById(R.id.genero);

        // Obtener los datos de la canción del Intent
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        titulo.setText(intent.getStringExtra("titulo"));
        artista.setText(intent.getStringExtra("artista"));
        album.setText(intent.getStringExtra("album"));
        fecha.setText(intent.getStringExtra("fecha"));
        duracion.setText(intent.getStringExtra("duracion"));
        genero.setText(intent.getStringExtra("genero"));
    }

    public void volverAtras(View view) {
        finish();
    }

    public void borrarCancion(View view) {
        if (id != -1) {
            new AlertDialog.Builder(this)
                    .setMessage("¿Estás seguro que quieres borrar esta canción?")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            executorService.execute(() -> {
                                AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                                Song song = new Song();
                                song.setId(id);
                                db.songDao().deleteSong(song);

                                runOnUiThread(() -> {
                                    // Enviar resultado a MainActivity para recargar la lista de canciones
                                    Intent resultIntent = new Intent();
                                    setResult(RESULT_OK, resultIntent);
                                    finish();
                                });
                            });
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }
}