package database;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Cancion")
public class Song {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "titulo")
    public String titulo;
    @ColumnInfo (name = "artista")
    public String artista;

    @ColumnInfo (name = "album")
    public String album;

    @ColumnInfo (name = "fecha")
    public String fecha;

    @ColumnInfo (name = "duracion")
    public String duracion;

    @ColumnInfo (name = "genero")
    public String genero;

    public Song() {
    }

    public Song(String titulo, String artista, String album, String fecha, String duracion, String genero) {
        this.titulo = titulo;
        this.artista = artista;
        this.album = album;
        this.fecha = fecha;
        this.duracion = duracion;
        this.genero = genero;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getArtista() {
        return artista;
    }

    public String getAlbum() {
        return album;
    }

    public String getFecha() {
        return fecha;
    }

    public String getDuracion() {
        return duracion;
    }

    public String getGenero() {
        return genero;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

}