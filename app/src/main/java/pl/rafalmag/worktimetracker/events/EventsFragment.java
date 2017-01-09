package pl.rafalmag.worktimetracker.events;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(DATE_FORMAT);

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
                table.addView(createTableRow(event));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not init events fragment, because of "
                    + e.getMessage(), e);
        }
        return view;
    }

    @NonNull
    private TableRow createTableRow(Event event) {
        TableRow tr = new TableRow(getActivity());
        tr.addView(createDateText(event));
        tr.addView(createDataText(event));
        return tr;
    }

    @NonNull
    private TextView createDateText(Event event) {
        TextView dateText = new TextView(getActivity());
        dateText.setText(DATE_TIME_FORMATTER.print(event.getDate().getTime()));
        return dateText;
    }

    @NonNull
    private TextView createDataText(Event event) {
        TextView dataText = new TextView(getActivity());
        dataText.setText(new EventParser().parseEvent(event).toString());
        return dataText;
    }

}
