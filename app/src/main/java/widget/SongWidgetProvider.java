package widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.proyecto.R;

import java.util.List;

import api.SongApi;
import database.Song;

public class SongWidgetProvider extends AppWidgetProvider {

    private static int currentPage = 0;
    private static final int SONGS_PER_PAGE = 5;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        // Llama a la API para obtener las canciones
        new Thread(() -> {
            try {
                List<Song> songs = SongApi.getSongs(-1); // -1 para obtener todas las canciones
                int totalPages = (int) Math.ceil((double) songs.size() / SONGS_PER_PAGE);

                // Asegúrate de que la página actual esté dentro de los límites
                currentPage = Math.max(0, Math.min(currentPage, totalPages - 1));

                // Obtén las canciones de la página actual
                int start = currentPage * SONGS_PER_PAGE;
                int end = Math.min(start + SONGS_PER_PAGE, songs.size());
                List<Song> pageSongs = songs.subList(start, end);

                StringBuilder songList = new StringBuilder();
                for (Song song : pageSongs) {
                    songList.append(song.getTitulo()).append(" - ").append(song.getArtista()).append("\n");
                }

                // Actualiza el widget con las canciones
                views.setTextViewText(R.id.widget_title, "Página " + (currentPage + 1) + " de " + totalPages);
                views.setTextViewText(R.id.widget_list, songList.toString());

                // Configura los botones de navegación
                Intent prevIntent = new Intent(context, SongWidgetProvider.class);
                prevIntent.setAction("ACTION_PREV_PAGE");
                PendingIntent prevPendingIntent = PendingIntent.getBroadcast(context, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                views.setOnClickPendingIntent(R.id.widget_prev_button, prevPendingIntent);

                Intent nextIntent = new Intent(context, SongWidgetProvider.class);
                nextIntent.setAction("ACTION_NEXT_PAGE");
                PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                views.setOnClickPendingIntent(R.id.widget_next_button, nextPendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, views);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if ("ACTION_PREV_PAGE".equals(intent.getAction())) {
            currentPage--;
        } else if ("ACTION_NEXT_PAGE".equals(intent.getAction())) {
            currentPage++;
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName widget = new ComponentName(context, SongWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(widget);

        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }
}