package com.example.proyecto;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class detallesCancion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_cancion);

        TextView titulo = findViewById(R.id.titulo);
        TextView artista = findViewById(R.id.artista);
        TextView album = findViewById(R.id.album);
        TextView fecha = findViewById(R.id.fecha);
        TextView duracion = findViewById(R.id.duracion);
        TextView genero = findViewById(R.id.genero);

        // Obtener los datos de la canción del Intent
        titulo.setText(getIntent().getStringExtra("titulo"));
        artista.setText(getIntent().getStringExtra("artista"));
        album.setText(getIntent().getStringExtra("album"));
        fecha.setText(getIntent().getStringExtra("fecha"));
        duracion.setText(getIntent().getStringExtra("duracion"));
        genero.setText(getIntent().getStringExtra("genero"));
    }

    public void volverAtras(View view) {
        finish();
    }
}