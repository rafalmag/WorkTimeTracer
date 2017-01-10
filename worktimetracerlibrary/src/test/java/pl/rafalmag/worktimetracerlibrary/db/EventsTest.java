package pl.rafalmag.worktimetracerlibrary.db;


import android.util.Log;

import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import pl.rafalmag.worktimetracerlibrary.EventSourcingPersistenceManager;
import pl.rafalmag.worktimetracerlibrary.Time;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class EventsTest {

    @Before
    public void test() {
        PowerMockito.mockStatic(Log.class);
    }

    @Test
    public void shouldProcessBasicEvents() throws Exception {
        // given
        List<Event> events = new ArrayList<>();
        events.add(new WorkTimeUpdatedEvent(Hours.EIGHT.toStandardMinutes()));
        events.add(new OvertimeUpdatedEvent(Minutes.ZERO, Minutes.ZERO));
        events.add(new StartStopUpdatedEvent(new Time(8, 0), new Time(16, 0)));
        EventSourcingPersistenceManager.ValueHolder valueHolder = new EventSourcingPersistenceManager.ValueHolder();
        // when
        for (Event event : events) {
            event.applyTo(valueHolder);
        }
        // then
        assertThat(valueHolder.getWorkTime(), equalTo(Hours.EIGHT.toStandardMinutes()));
        assertThat(valueHolder.getOvertime(), equalTo(Minutes.ZERO));
        assertThat(valueHolder.getStartTime(), equalTo(new Time(8, 0)));
        assertThat(valueHolder.getStopTime(), equalTo(new Time(16, 0)));
    }

    @Test
    public void shouldProcessComplexEvents() throws Exception {
        // given
        List<Event> events = new ArrayList<>();
        events.add(new WorkTimeUpdatedEvent(Hours.EIGHT.toStandardMinutes()));
        events.add(new OvertimeUpdatedEvent(Minutes.ZERO, Minutes.ZERO));
        events.add(new StartStopUpdatedEvent(new Time(8, 0), new Time(15, 0))); // 8h diff
        events.add(new OvertimeUpdatedEvent(Minutes.ZERO, Minutes.ONE)); // +1min OT
        events.add(new LogTimeEvent(new Time(8, 0), new Time(15, 0), Hours.EIGHT.toStandardMinutes(), Minutes.ONE)); // -59min OT
        events.add(new WorkTimeUpdatedEvent(Hours.SIX.toStandardMinutes()));
        events.add(new StartStopUpdatedEvent(new Time(1, 0), new Time(8, 0))); // 7h diff
        events.add(new LogTimeEvent(new Time(1, 0), new Time(8, 0), Hours.SIX.toStandardMinutes(), Minutes.minutes(-59))); // +1min OT
        EventSourcingPersistenceManager.ValueHolder valueHolder = new EventSourcingPersistenceManager.ValueHolder();
        // when
        for (Event event : events) {
            event.applyTo(valueHolder);
        }
        // then
        assertThat(valueHolder.getWorkTime(), equalTo(Hours.SIX.toStandardMinutes()));
        assertThat(valueHolder.getOvertime(), equalTo(Minutes.ONE));
        assertThat(valueHolder.getStartTime(), equalTo(new Time(1, 0)));
        assertThat(valueHolder.getStopTime(), equalTo(new Time(8, 0)));
    }
}
