package database;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Song.class, User.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SongDao songDao();
    private static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);
    private static volatile AppDatabase INSTANCE;


    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "db_canciones")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.d("AppDatabase", "Base de datos creada, añadiendo valores default");
            databaseWriteExecutor.execute(() -> {
                // Insert default songs
                SongDao dao = INSTANCE.songDao();
                Song song1 = new Song();
                song1.setTitulo("Bohemian Rhapsody");
                song1.setArtista("Queen");
                song1.setAlbum("A Night at the Opera");
                song1.setFecha("1975/10/31");
                song1.setDuracion("5:55");
                song1.setGenero("Rock");
                dao.insertSong(song1);

                Song song2 = new Song();
                song2.setTitulo("Bad");
                song2.setArtista("Michael Jackson");
                song2.setAlbum("Bad");
                song2.setFecha("1987/09/07");
                song2.setDuracion("4:07");
                song2.setGenero("Pop");
                dao.insertSong(song2);

                Song song3 = new Song();
                song3.setTitulo("Die Young");
                song3.setArtista("Kesha");
                song3.setAlbum("Warrior");
                song3.setFecha("2012/11/25");
                song3.setDuracion("3:33");
                song3.setGenero("Pop");
                dao.insertSong(song3);

                Song song4 = new Song();
                song4.setTitulo("Piano man");
                song4.setArtista("Billy Joel");
                song4.setAlbum("Null");
                song4.setFecha("1973/11/02");
                song4.setDuracion("5:38");
                song4.setGenero("Rock");
                dao.insertSong(song4);

                Song song5 = new Song();
                song5.setTitulo("Dont Stop me Now");
                song5.setArtista("Queen");
                song5.setAlbum("Jazz");
                song5.setFecha("1978/01/26");
                song5.setDuracion("3:29");
                song5.setGenero("Rock");
                dao.insertSong(song5);

                Song song6 = new Song();
                song6.setTitulo("Another One Bites the Dust");
                song6.setArtista("Queen");
                song6.setAlbum("The Game");
                song6.setFecha("1980/06/30");
                song6.setDuracion("3:36");
                song6.setGenero("Rock");
                dao.insertSong(song6);

                Song song7 = new Song();
                song7.setTitulo("More than a Feeling");
                song7.setArtista("Boston");
                song7.setAlbum("Boston");
                song7.setFecha("1976/08/25");
                song7.setDuracion("4:45");
                song7.setGenero("Rock");
                dao.insertSong(song7);

                Song song8 = new Song();
                song8.setTitulo("Mr. Blue Sky");
                song8.setArtista("Electric Light Orchestra");
                song8.setAlbum("Out of the Blue");
                song8.setFecha("1977/10/03");
                song8.setDuracion("5:05");
                song8.setGenero("Rock");
                dao.insertSong(song8);
            });
        }
    };
}