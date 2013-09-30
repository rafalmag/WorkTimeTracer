package pl.rafalmag.worktimetracker;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import android.test.AndroidTestCase;
import android.test.ApplicationTestCase;

import org.joda.time.Minutes;
import org.junit.Test;

import pl.rafalmag.worktimetracker.WorkTimeTrackerApp;

/**
 * User: rafalmag
 * Date: 17.04.13
 * Time: 21:26
 */
public class WorkTimeTracerAppTest extends ApplicationTestCase<WorkTimeTrackerApp> {

    private final WorkTimeTrackerApp app;

    public WorkTimeTracerAppTest() {
        super(WorkTimeTrackerApp.class);
        createApplication();
        app = (WorkTimeTrackerApp) getApplication();
    }

    public void testNotNull() {
        assertNotNull(app);
    }

    public void testFail() {
        fail();
    }

//    //    @Test
//    public void testSomething() {
//        //given
//        WorkTimeTrackerApp app = (WorkTimeTrackerApp) getApplication();
//        //when
////        String text = DateUtils.minutesToText(minutes);
//        //then
////        assertThat(text, equalTo("1 h 1 min"));
//    }
}
