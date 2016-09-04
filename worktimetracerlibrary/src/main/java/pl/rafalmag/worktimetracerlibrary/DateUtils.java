package pl.rafalmag.worktimetracerlibrary;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Minutes;

import java.util.Calendar;

/**
 * User: rafalmag
 * Date: 11.04.13
 * Time: 23:08
 */
public class DateUtils {
    public static Minutes diff(Calendar startDate, Calendar endDate) {
        DateTime startDateTime = new DateTime(startDate.getTimeInMillis());
        DateTime endDateTime = new DateTime(endDate.getTimeInMillis());
        return diff(startDateTime, endDateTime);
    }

    private static Minutes diff(DateTime startDateTime, DateTime endDateTime) {
        if (startDateTime.isAfter(endDateTime)) {
            startDateTime = startDateTime.minusDays(1);
        }
        Duration duration = new Duration(startDateTime, endDateTime);
        return duration.toStandardMinutes();
    }

    public static String minutesToText(Minutes minutes) {
        int totalMinutes = minutes.getMinutes();
        int days = Math.abs(totalMinutes) / 60 / 24;
        int hours = Math.abs(totalMinutes) / 60 - days * 24;
        int mins = Math.abs(totalMinutes) % 60;
        String sb = (totalMinutes < 0 ? "-" : "") +
                (days == 0 ? "" : days + " day ") +
                (hours == 0 ? "" : hours + " h ") +
                (mins == 0 ? "" : mins + " min ");

        String result = sb.trim();
        return result.isEmpty() ? "0 min" : result;
    }

    public static Minutes diff(Time startTime, Time stopTime) {
        return diff(startTime.toCalendar(), stopTime.toCalendar());
    }
}
