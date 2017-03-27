package pl.rafalmag.worktimetracerlibrary.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// to start an Alarm When the Device Boots
public class WorkEndNotificationBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // TODO Set the alarm here.
        }
    }
}
