package pl.rafalmag.worktimetracerlibrary.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.joda.time.Hours;

import java.util.Date;
import java.util.Random;

import pl.rafalmag.worktimetracerlibrary.R;

public class WorkEndNotificationPublisher extends BroadcastReceiver {

    private static final String TAG = WorkEndNotificationPublisher.class.getCanonicalName();

        public static final long AUTO_DISMISS_PERIOD_IN_MS = Hours.FOUR.toStandardDuration().getMillis();
    // for debug -> dismiss after 1 min
    // public static final long AUTO_DISMISS_PERIOD_IN_MS = Minutes.ONE.toStandardDuration().getMillis();

    public void onReceive(Context context, Intent intent) {
        // TODO check if should fire notification (ex. if the expected end time is in the past
        // and work was not logged today
        final int notifyId = new Random().nextInt();
        // https://material.io/guidelines/patterns/notifications.html
        // no peeking
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        // permit to dismiss
        builder.setOngoing(false);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        builder.setSmallIcon(R.drawable.stop);
        builder.setContentTitle(context.getString(R.string.endWork));
        // Timestamp
        builder.setWhen(new Date().getTime());
        builder.setShowWhen(true);
        // on click action
        builder.setContentIntent(createClickedIntent(context));
        // remove notification if clicked
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_EVENT);
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        final NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        setAutoDismiss(notifyId, mNotificationManager);

        Notification notification = builder.build();
        // Making a sound or vibration
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;

        Log.d(TAG, "Publishing notification " + notifyId);
        mNotificationManager.notify(notifyId, notification);
    }

    private void setAutoDismiss(final int notifyId, final NotificationManager mNotificationManager) {
        final Handler handler = new Handler();
        final Runnable notificationCancellingRunnable = new Runnable() {
            public void run() {
                Log.d(TAG, "Cancelling notification " + notifyId + " (auto-dismissal)");
                mNotificationManager.cancel(notifyId);
            }
        };
        handler.postDelayed(notificationCancellingRunnable, AUTO_DISMISS_PERIOD_IN_MS);
    }

    @Nullable
    private PendingIntent createClickedIntent(Context ctx) {
        Class<?> workTimeTrackerClass = ((MainActivityClassProvider) ctx.getApplicationContext())
                .getMainActivityClass();
        Intent resultIntent = new Intent(ctx, workTimeTrackerClass);
        return PendingIntent.getActivity(ctx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
