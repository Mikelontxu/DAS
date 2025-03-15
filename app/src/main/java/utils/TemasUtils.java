package utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import com.example.proyecto.R;

public class TemasUtils {
    public static void applyTheme(Context context, View rootView) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String theme = prefs.getString("theme", "light");
        if ("dark".equals(theme)) {
            context.setTheme(R.style.AppTheme_Dark);
            rootView.setBackgroundColor(context.getResources().getColor(R.color.dark_background));
        } else {
            context.setTheme(R.style.AppTheme_Light);
            rootView.setBackgroundColor(context.getResources().getColor(R.color.light_background));
        }
    }
}