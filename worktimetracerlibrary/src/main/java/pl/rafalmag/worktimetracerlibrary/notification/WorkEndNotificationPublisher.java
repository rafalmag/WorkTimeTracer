package pl.rafalmag.worktimetracerlibrary.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import org.joda.time.Hours;

import java.util.Date;
import java.util.Random;

import pl.rafalmag.worktimetracerlibrary.R;

public class WorkEndNotificationPublisher extends BroadcastReceiver{

    public static final long AUTO_DISMISS_PERIOD_IN_MS = Hours.FOUR.toStandardDuration().getMillis();
    public static final int WTT_REQUEST_CODE = 1;

    // TODO get from context
    private Class<?> workTimeTrackerClass;

    public void onReceive(Context context, Intent intent) {
        final int notifyId = new Random().nextInt();
        // https://material.io/guidelines/patterns/notifications.html
        // no peeking
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        // permit to dismiss
        builder.setOngoing(false);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        builder.setLargeIcon(bitmap);
        // TODO smallicon
        // Timestamp
        builder.setWhen(new Date().getTime());
        builder.setShowWhen(true);
//        PendingIntent notiClickedIntent = getClickedIntent(context);
//        builder.setContentIntent(notiClickedIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_EVENT);
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        final NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        autoDismiss(context, notifyId, builder, mNotificationManager);

        Notification notification = builder.build();
        // Making a sound or vibration
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;

        mNotificationManager.notify(notifyId, notification);
    }

    private void autoDismiss(Context context, final int notifyId, NotificationCompat.Builder builder, final NotificationManager mNotificationManager) {
        final Handler handler = new Handler();
        final Runnable notificationCancellingRunnable = new Runnable() {
            public void run() {
                mNotificationManager.cancel(notifyId);
            }
        };
        handler.postDelayed(notificationCancellingRunnable, AUTO_DISMISS_PERIOD_IN_MS);

        class MyBroadcastReceiver extends BroadcastReceiver {

            @Override
            public void onReceive(Context context, Intent intent) {
                handler.removeCallbacks(notificationCancellingRunnable);
            }
        }

        Intent notiDeleted = new Intent(context, MyBroadcastReceiver.class);
        builder.setDeleteIntent(PendingIntent.getBroadcast(context, 0, notiDeleted, 0));
    }

    @Nullable
    private PendingIntent getClickedIntent(Context ctx) {
        Intent notiClicked = new Intent(ctx, workTimeTrackerClass);
        return PendingIntent.getBroadcast(ctx, WTT_REQUEST_CODE, notiClicked, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
