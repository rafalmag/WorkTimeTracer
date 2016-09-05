package pl.rafalmag.worktimetracker.wear;

import android.app.Activity;

/**
 * @see <a href="https://looksok.wordpress.com/2013/11/02/viewpager-with-detailed-fragment-lifecycle-onresumefragment-including-source-code/">description</a>
 */
public interface FragmentLifecycle {

	void onPauseFragment(Activity activity);

    void onResumeFragment(Activity activity);

}