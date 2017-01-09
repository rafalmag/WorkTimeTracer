package pl.rafalmag.worktimetracerlibrary.db;

import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class WorkTimeUpdatedEventTest {
    @Test
    public void should_toString() throws Exception {
        // given
        Minutes workTime = Hours.EIGHT.toStandardMinutes();
        WorkTimeUpdatedEvent workTimeUpdatedEvent = new WorkTimeUpdatedEvent(workTime);
        // when
        String toString = workTimeUpdatedEvent.toString();
        // then
        assertThat(toString, equalTo("New work time 8 h"));
    }

}