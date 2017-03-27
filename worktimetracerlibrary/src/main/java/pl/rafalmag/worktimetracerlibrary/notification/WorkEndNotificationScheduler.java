package pl.rafalmag.worktimetracerlibrary.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

public class WorkEndNotificationScheduler {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    public void scheduleWorkEndNotification(Context context, DateTime expectedEndTime){
        if(expectedEndTime.isBeforeNow()){
            cancelWorkEndNotification(context);
            return;
        }
        // alarm
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WorkEndNotificationPublisher.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMgr.set(AlarmManager.RTC_WAKEUP, expectedEndTime.getMillis(), alarmIntent);

//        // 60 sec
//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime() + 60 * 1000, alarmIntent);

        // to enable starting an Alarm When the Device Boots
        setBootReceiverState(context, PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
    }

    private void setBootReceiverState(Context context, int componentEnabledStateEnabled) {
        ComponentName receiver = new ComponentName(context, this.getClass());
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, componentEnabledStateEnabled, PackageManager.DONT_KILL_APP);
    }

    public void cancelWorkEndNotification(Context context){
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
        // to prevent unnecessary setting the alarm when the device boots
        setBootReceiverState(context, PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
    }


}
