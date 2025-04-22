package alarma;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import com.example.proyecto.R;

public class Reminder extends BroadcastReceiver {

    private static final String CHANNEL_ID = "daily_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Crear el canal de notificación (solo necesario para Android 8.0+)
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Recordatorio Diario",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Crear y mostrar la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_info) // Cambia el icono según tu proyecto
                .setContentTitle("¡Es hora de escuchar música!")
                .setContentText("Recuerda escuchar tus canciones favoritas.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }
}