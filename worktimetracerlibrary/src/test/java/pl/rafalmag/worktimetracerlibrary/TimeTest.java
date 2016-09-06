package pl.rafalmag.worktimetracerlibrary;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Calendar;

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
        assertThat(calendar.get(Calendar.HOUR_OF_DAY), Matchers.equalTo(hours));
        assertThat(calendar.get(Calendar.MINUTE), Matchers.equalTo(minutes));
        assertThat(calendar.get(Calendar.DAY_OF_MONTH), Matchers.equalTo(now.get(Calendar.DAY_OF_MONTH)));
        assertThat(calendar.get(Calendar.MONTH), Matchers.equalTo(now.get(Calendar.MONTH)));
        assertThat(calendar.get(Calendar.YEAR), Matchers.equalTo(now.get(Calendar.YEAR)));
    }

}
