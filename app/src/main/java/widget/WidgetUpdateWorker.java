package widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import widget.SongWidgetProvider;

public class WidgetUpdateWorker extends Worker {

    public WidgetUpdateWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName widget = new ComponentName(context, SongWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(widget);

        if (appWidgetIds != null && appWidgetIds.length > 0) {
            new SongWidgetProvider().onUpdate(context, appWidgetManager, appWidgetIds);
        }

        return Result.success();
    }
}