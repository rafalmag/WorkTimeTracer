package pl.rafalmag.worktimetracerlibrary;

import java.util.Observable;

public class TimeHolder extends Observable {

    private Time time = new Time(0,0);

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
        setChanged();
        notifyObservers(time);
    }
}
