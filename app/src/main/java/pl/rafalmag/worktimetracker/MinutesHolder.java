package pl.rafalmag.worktimetracker;

import java.util.Observable;

import org.joda.time.Minutes;

/**
 * User: rafalmag
 * Date: 12.04.13
 * Time: 20:45
 */
public class MinutesHolder extends Observable {

    private Minutes minutes = Minutes.minutes(0);

    public Minutes getMinutes() {
        return minutes;
    }

    public void setMinutes(Minutes minutes) {
        this.minutes = minutes;
        setChanged();
        notifyObservers(minutes);
    }
}
