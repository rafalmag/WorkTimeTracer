package pl.rafalmag.worktimetracker;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class TodayDateTest {

	@Test
	public void should_convert_hours_minutes_24h() {
		//given
		int hours = 14;
		int minutes = 10;
		TodayDate todayDate = new TodayDate(hours,minutes);
		Calendar now = Calendar.getInstance();
		//when
        Calendar actual = todayDate.toCalendar();
		//then
		assertThat(actual.get(Calendar.HOUR_OF_DAY),equalTo(hours));
        assertThat(actual.get(Calendar.MINUTE),equalTo(minutes));
        assertThat(actual.get(Calendar.DAY_OF_MONTH),equalTo(now.get(Calendar.DAY_OF_MONTH)));
        assertThat(actual.get(Calendar.MONTH),equalTo(now.get(Calendar.MONTH)));
        assertThat(actual.get(Calendar.YEAR),equalTo(now.get(Calendar.YEAR)));
	}

}
