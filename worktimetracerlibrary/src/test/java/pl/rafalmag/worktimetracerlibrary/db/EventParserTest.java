package pl.rafalmag.worktimetracerlibrary.db;

import org.junit.Test;

import pl.rafalmag.worktimetracerlibrary.Time;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class EventParserTest {

    @Test
    public void shouldParserParseEvents() {
        // given
        Time start = new Time(13, 12);
        Time stop = new Time(16, 17);
        StartStopEvent startStopEvent = new StartStopEvent(start, stop);
        // when
        StartStopEvent newStartStopEvent = (StartStopEvent) new EventParser().parseEvent(startStopEvent);
        // then
        assertThat(newStartStopEvent.getId(), equalTo(startStopEvent.getId()));
        assertThat(newStartStopEvent.getData(), equalTo(startStopEvent.getData()));
        assertThat(newStartStopEvent.getDate(), equalTo(startStopEvent.getDate()));
        assertThat(newStartStopEvent.getVersion(), equalTo(startStopEvent.getVersion()));
        assertThat(newStartStopEvent.getStartTime(), equalTo(start));
        assertThat(newStartStopEvent.getStopTime(), equalTo(stop));
    }
}