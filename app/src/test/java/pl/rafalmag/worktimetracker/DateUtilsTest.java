package pl.rafalmag.worktimetracker;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.joda.time.Minutes;
import org.junit.Test;

/**
 * User: rafalmag
 * Date: 11.04.13
 * Time: 23:09
 */
public class DateUtilsTest {


    @Test
    public void should_convert_minutes() {
        //given
        Minutes minutes = Minutes.minutes(60 + 1);
        //when
        String text = DateUtils.minutesToText(minutes);
        //then
        assertThat(text, equalTo("1 h 1 min"));
    }

    @Test
    public void should_convert_0_minutes_to_0_mins() {
        //given
        Minutes minutes = Minutes.minutes(0);
        //when
        String text = DateUtils.minutesToText(minutes);
        //then
        assertThat(text, equalTo("0 min"));
    }

    @Test
    public void should_convert_day_minutes() {
        //given
        Minutes minutes = Minutes.minutes(60 * 24);
        //when
        String text = DateUtils.minutesToText(minutes);
        //then
        assertThat(text, equalTo("1 day"));
    }

    @Test
    public void should_calc_diff_between_2_dates_same_day() {
        //given
        Time startDate = new Time(8, 20);
        Time endDate = new Time(16, 19);
        //when
        Minutes diff = DateUtils.diff(startDate, endDate);
        String diffText = DateUtils.minutesToText(diff);
        //then
        assertThat(diffText, equalTo("7 h 59 min"));
    }

    @Test
    public void should_calc_diff_between_2_dates_diff_day() {
        //given
        Time startDate = new Time(16, 20);
        Time endDate = new Time(15, 19);
        //when
        Minutes diff = DateUtils.diff(startDate, endDate);
        String diffText = DateUtils.minutesToText(diff);
        //then
        assertThat(diffText, equalTo("22 h 59 min"));
    }
}
