package pl.rafalmag.worktimetracerlibrary.notification;

import android.app.Activity;

public interface MainActivityClassProvider {

    Class<? extends Activity> getMainActivityClass();
}
