package database;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
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

    public int getId() {
        return id;
    }

    public String getTitle() {
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

    public void setTitle(String titulo) {
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