package utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import com.example.proyecto.R;

public class TemasUtils {
    public static void applyTheme(Context context, View rootView) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String theme = prefs.getString("theme", "white");
        switch (theme) {
            case "light_green":
                rootView.setBackgroundColor(context.getResources().getColor(R.color.light_green_background));
                break;
            case "light_purple":
                rootView.setBackgroundColor(context.getResources().getColor(R.color.light_purple_background));
                break;
            case "gray":
                rootView.setBackgroundColor(context.getResources().getColor(R.color.gray_background));
                break;
            case "white":
            default:
                rootView.setBackgroundColor(context.getResources().getColor(R.color.white_background));
                break;
        }
    }
}