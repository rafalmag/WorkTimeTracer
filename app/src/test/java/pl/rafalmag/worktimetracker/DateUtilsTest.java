package pl.rafalmag.worktimetracker;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Calendar;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: rafalmag
 * Date: 11.04.13
 * Time: 23:09
 */
public class DateUtilsTest {

    @Test
    public void should_calc_diff_between_2_dates_same_day(){
        //given
        TodayDate startDate = new TodayDate(8, 20);
        TodayDate endDate = new TodayDate(16, 19);
        //when
        String diff = DateUtils.diff(startDate, endDate);
        //then
        assertThat(diff, equalTo("7 h 59 min"));
    }

    @Test
    public void should_calc_diff_between_2_dates_diff_day(){
        //given
        TodayDate startDate = new TodayDate(16, 20);
        TodayDate endDate = new TodayDate(15, 19);
        //when
        String diff = DateUtils.diff(startDate, endDate);
        //then
        assertThat(diff, equalTo("22 h 59 min"));
    }
}
