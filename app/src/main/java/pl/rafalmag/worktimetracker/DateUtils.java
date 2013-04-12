package pl.rafalmag.worktimetracker;

import java.util.Calendar;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * User: rafalmag
 * Date: 11.04.13
 * Time: 23:08
 */
public class DateUtils {
    public static String diff(Calendar startDate, Calendar endDate) {
        DateTime startDateTime = new DateTime(startDate.getTimeInMillis());
        DateTime endDateTime = new DateTime(endDate.getTimeInMillis());
        return diff(startDateTime, endDateTime);
    }

    private static String diff(DateTime startDateTime, DateTime endDateTime) {
        if (startDateTime.isAfter(endDateTime)) {
            startDateTime = startDateTime.minusDays(1);
        }
        Duration duration = new Duration(startDateTime, endDateTime);
        long days = duration.getStandardDays();
        long hours = duration.getStandardHours() - days * 24;
        long minutes = duration.getStandardMinutes() - hours * 60;
        StringBuilder sb = new StringBuilder();
        sb.append(days == 0 ? "" : days + " day(s) ");
        sb.append(hours == 0 ? "" : hours + " h ");
        sb.append(minutes == 0 ? "" : minutes + " min ");
        return sb.toString().trim();
    }

    public static String diff(TodayDate startDate, TodayDate stopDate) {
        return diff(startDate.toCalendar(), stopDate.toCalendar());
    }
}
