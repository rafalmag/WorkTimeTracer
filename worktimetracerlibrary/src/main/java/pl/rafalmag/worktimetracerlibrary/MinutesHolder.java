package pl.rafalmag.worktimetracerlibrary;

import org.joda.time.Minutes;

import java.util.Observable;

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
