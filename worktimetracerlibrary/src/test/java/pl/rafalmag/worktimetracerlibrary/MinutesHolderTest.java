package pl.rafalmag.worktimetracerlibrary;

import org.joda.time.Minutes;
import org.junit.Test;

import java.util.Observable;
import java.util.Observer;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

public class MinutesHolderTest {

    @Test(timeout = 1000L)
    public void shouldNotLoopForever() throws Exception {
        // given
        final MinutesHolder minutesHolder = new MinutesHolder();
        // when
        minutesHolder.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                Minutes minutes = (Minutes) data;
                assertThat(minutesHolder, sameInstance(observable));
                observable.deleteObserver(this);
                // any method that would trigger the observer
                minutesHolder.setMinutes(minutes);
                observable.addObserver(this);
            }
        });
        minutesHolder.setMinutes(Minutes.minutes(1));
        //then
        assertThat(minutesHolder.getMinutes().getMinutes(), equalTo(1));
        // no StackOverflowError

    }
}