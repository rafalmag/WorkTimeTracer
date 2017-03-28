package pl.rafalmag.worktimetracerlibrary.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.util.Log;

import org.joda.time.DateTime;

public class WorkEndNotificationScheduler {

    private static final String TAG = WorkEndNotificationScheduler.class.getCanonicalName();

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    public void scheduleWorkEndNotification(Context context, DateTime expectedEndTime) {
        // cancel previous notifications
        cancelWorkEndNotification(context);
        if (expectedEndTime.isBeforeNow()) {
            return;
        }
        // alarm
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WorkEndNotificationPublisher.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Log.i(TAG, "Scheduling notification alarm at " + expectedEndTime);
//        alarmMgr.set(AlarmManager.RTC_WAKEUP, expectedEndTime.getMillis(), alarmIntent);

        // 10 sec for debug
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 10 * 1000, alarmIntent);

        // to enable starting an Alarm When the Device Boots
        setBootReceiverState(context, PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
    }

    private void setBootReceiverState(Context context, int componentEnabledStateEnabled) {
        ComponentName receiver = new ComponentName(context, WorkEndNotificationBootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, componentEnabledStateEnabled, PackageManager.DONT_KILL_APP);
    }

    public void cancelWorkEndNotification(Context context) {
        if (alarmMgr != null) {
            Log.i(TAG, "Cancelling notification alarm");
            alarmMgr.cancel(alarmIntent);
        }
        // to prevent unnecessary setting the alarm when the device boots
        setBootReceiverState(context, PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
    }


}
