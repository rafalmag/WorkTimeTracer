package pl.rafalmag.worktimetracerlibrary.db;

import org.joda.time.Minutes;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;


public class OvertimeUpdatedEventTest {

    @Test
    public void shouldToString() throws Exception {
        // given
        Minutes oldOvertime = Minutes.minutes(90);
        Minutes newOvertime = Minutes.minutes(60);
        OvertimeUpdatedEvent overtimeUpdatedEvent = new OvertimeUpdatedEvent(oldOvertime, newOvertime);
        // when
        String toString = overtimeUpdatedEvent.toString();
        // then
        assertThat(toString, equalTo("New overtime 1 h (-30 min)"));
    }

    @Test
    public void shouldToStringEqualValues() throws Exception {
        // given
        Minutes oldOvertime = Minutes.minutes(60);
        Minutes newOvertime = Minutes.minutes(60);
        OvertimeUpdatedEvent overtimeUpdatedEvent = new OvertimeUpdatedEvent(oldOvertime, newOvertime);
        // when
        String toString = overtimeUpdatedEvent.toString();
        // then
        assertThat(toString, equalTo("New overtime 1 h"));
    }
}