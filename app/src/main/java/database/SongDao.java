package database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SongDao {
    @Query("SELECT * FROM Cancion")
    List<Song> getAllSongs();

    @Insert
    void insertSong(Song song);

    @Delete
    void deleteSong(Song song);

}