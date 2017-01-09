package pl.rafalmag.worktimetracerlibrary;

import org.junit.Test;

import java.util.Calendar;

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

    @Test
    public void should_toString() throws Exception {
        // given
        int hours = 7;
        int minutes = 1;
        Time time = new Time(hours,minutes);
        // when
        String toString = time.toString();
        // then
        assertThat(toString,equalTo("07:01"));

    }
}
