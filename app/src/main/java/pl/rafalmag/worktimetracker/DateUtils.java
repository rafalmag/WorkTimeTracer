package pl.rafalmag.worktimetracker;

import java.util.Calendar;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Minutes;

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
        int days = totalMinutes / 60 / 24;
        int hours = totalMinutes / 60 - days * 24;
        int mins = totalMinutes % 60;
//        long days = duration.getStandardDays();
//        long hours = duration.getStandardHours() - days * 24;
//        long minutes = duration.getStandardMinutes() - hours * 60;
        StringBuilder sb = new StringBuilder();
        sb.append(days == 0 ? "" : days + " day ");
        sb.append(hours == 0 ? "" : hours + " h ");
        sb.append(mins == 0 ? "" : mins + " min ");

        String result = sb.toString().trim();
        return result.isEmpty() ? "0 min" : result;
    }

    public static Minutes diff(Time startTime, Time stopTime) {
        return diff(startTime.toCalendar(), stopTime.toCalendar());
    }
}
