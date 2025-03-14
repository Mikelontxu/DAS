package database;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Song.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SongDao songDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .addCallback(sRoomDatabaseCallback)
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
            databaseWriteExecutor.execute(() -> {
                // Insert default songs
                SongDao dao = INSTANCE.songDao();
                Song song1 = new Song();
                song1.setTitle("Bohemian Rhapsody");
                song1.setArtista("Queen");
                song1.setAlbum("A Night at the Opera");
                song1.setFecha("1975-10-31");
                song1.setDuracion("5:55");
                song1.setGenero("Rock");
                dao.insertSong(song1);

                Song song2 = new Song();
                song2.setTitle("Bad");
                song2.setArtista("Michael Jackson");
                song2.setAlbum("Bad");
                song2.setFecha("1987-09-07");
                song2.setDuracion("4:07");
                song2.setGenero("Pop");
                dao.insertSong(song2);
            });
        }
    };
}