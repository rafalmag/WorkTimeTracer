package pl.rafalmag.worktimetracker;

import org.junit.Test;

import java.util.Calendar;

import pl.rafalmag.worktimetracker.Time;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TimeTest {

    @Test
    public void should_convert_hours_minutes_24h() {
        //given
        int hours = 14;
        int minutes = 10;
        Time time = new Time(hours, minutes);
        Calendar now = Calendar.getInstance();
        //when
        Calendar calendar = time.toCalendar();
        //then
        assertThat(calendar.get(Calendar.HOUR_OF_DAY), equalTo(hours));
        assertThat(calendar.get(Calendar.MINUTE), equalTo(minutes));
        assertThat(calendar.get(Calendar.DAY_OF_MONTH), equalTo(now.get(Calendar.DAY_OF_MONTH)));
        assertThat(calendar.get(Calendar.MONTH), equalTo(now.get(Calendar.MONTH)));
        assertThat(calendar.get(Calendar.YEAR), equalTo(now.get(Calendar.YEAR)));
    }

}
