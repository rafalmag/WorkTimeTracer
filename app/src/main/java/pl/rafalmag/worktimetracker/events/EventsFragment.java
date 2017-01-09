package pl.rafalmag.worktimetracker.events;

import android.app.Fragment;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.rafalmag.worktimetracerlibrary.db.Event;
import pl.rafalmag.worktimetracerlibrary.db.EventParser;
import pl.rafalmag.worktimetracerlibrary.db.WorkTimeTracerOpenHelper;
import pl.rafalmag.worktimetracker.R;

public class EventsFragment extends Fragment {

//    private static final String TAG = EventsFragment.class.getCanonicalName();
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @BindView(R.id.table)
    TableLayout table;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events, container, false);
        ButterKnife.bind(this, view);

        WorkTimeTracerOpenHelper workTimeTracerOpenHelper =
                OpenHelperManager.getHelper(getActivity(), WorkTimeTracerOpenHelper.class);

        try {
            Dao<Event, Integer> eventDao = workTimeTracerOpenHelper.getEventDao();
            List<Event> events = eventDao.queryBuilder().orderBy("date", true).query();
            for (Event event : events) {
                TableRow tr = new TableRow(getActivity());
                TextView dateText = new TextView(getActivity());
                dateText.setText(DateFormat.format(DATE_FORMAT, event.getDate()));
                tr.addView(dateText);
                TextView dataText = new TextView(getActivity());
                dataText.setText(getDataText(event));
                tr.addView(dataText);
                table.addView(tr);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not init events fragment, because of "
                    + e.getMessage(), e);
        }
        return view;
    }

    private String getDataText(Event event) {
        return new EventParser().parseEvent(event).toString();
    }

}
