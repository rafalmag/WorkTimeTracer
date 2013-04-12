package pl.rafalmag.worktimetracker;

import java.util.Calendar;
import java.util.Date;

public class Time {

    private final int hours;
    private final int minutes;

    public Time(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public Date toDate() {
        return toCalendar().getTime();
    }

    public Calendar toCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        return calendar;
    }

}
