package com.example.prm392mnlv.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.prm392mnlv.R;

import me.leolin.shortcutbadger.ShortcutBadger;

public class NotificationHelper {

    private static final String CHANNEL_ID = "cart_channel_id";
    private static final int NOTIFICATION_ID = 1001;

    public static void showCartNotification(Context context, int cartItemCount) {
        // 1. Create Notification Channel (API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Cart Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }

        // 2. Create Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Cart Update")
                .setContentText("You have " + cartItemCount + " item(s) in your cart.")
                .setSmallIcon(R.drawable.ic_cart) // icon nhỏ ở thanh trạng thái
                .setAutoCancel(true);

        // 3. Show Notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, builder.build());
        }

        // 4. Set Badge Count on App Icon
        ShortcutBadger.applyCount(context, cartItemCount);
    }

    // Optional: clear badge when needed
    public static void clearBadge(Context context) {
        ShortcutBadger.removeCount(context);
    }
}

