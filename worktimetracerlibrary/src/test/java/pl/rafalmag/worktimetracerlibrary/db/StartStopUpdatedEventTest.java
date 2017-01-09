package pl.rafalmag.worktimetracerlibrary.db;

import org.junit.Test;

import pl.rafalmag.worktimetracerlibrary.Time;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class StartStopUpdatedEventTest {

    @Test
    public void shouldToString() throws Exception {
        // given
        Time startTime = new Time(8,0);
        Time stopTime = new Time(16,0);
        StartStopUpdatedEvent startStopUpdatedEvent = new StartStopUpdatedEvent(startTime, stopTime);
        // when
        String toString = startStopUpdatedEvent.toString();
        // then
        assertThat(toString, equalTo("New start 08:00, stop 16:00, diff 8 h"));
    }
}