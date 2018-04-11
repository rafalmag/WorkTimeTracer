package pl.rafalmag.worktimetracker.events;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.rafalmag.worktimetracerlibrary.db.Event;
import pl.rafalmag.worktimetracerlibrary.db.EventParser;
import pl.rafalmag.worktimetracerlibrary.db.WorkTimeTracerOpenHelper;
import pl.rafalmag.worktimetracker.R;

public class EventsFragment extends Fragment {

    private static final String TAG = EventsFragment.class.getCanonicalName();
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(DATE_FORMAT);

    @BindView(R.id.table)
    TableLayout table;

    @BindView(R.id.layout)
    LinearLayout layout;

    Map<String, CheckBox> types = new HashMap<>();
    private WorkTimeTracerOpenHelper workTimeTracerOpenHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events, container, false);
        ButterKnife.bind(this, view);

        workTimeTracerOpenHelper = OpenHelperManager.getHelper(getActivity(), WorkTimeTracerOpenHelper.class);
        Dao<Event, Integer> eventDao = workTimeTracerOpenHelper.getEventDao();
        try {
            initTypes(eventDao);
            initTable(eventDao);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not init events fragment, because of "
                    + e.getMessage(), e);
        }
        return view;
    }

    private void initTable(Dao<Event, Integer> eventDao) throws SQLException {
        int childCount = table.getChildCount();
        table.removeViews(1, childCount - 1);
        List<Event> events = eventDao
                .queryBuilder()
                .orderBy("date", false)
                .where().in("typeClass", getTypesToSelect())
                .query();
        for (Event event : events) {
            table.addView(createTableRow(event));
        }
    }

    @NonNull
    private Iterable<String> getTypesToSelect() {
        Set<String> typesToSelect = new HashSet<>();
        for (Map.Entry<String, CheckBox> entry : types.entrySet()) {
            if (entry.getValue().isChecked()) {
                typesToSelect.add(entry.getKey());
            }
        }
        return typesToSelect;
    }

    private void initTypes(Dao<Event, Integer> eventDao) throws SQLException {
        List<String[]> results = eventDao.queryRaw("select distinct typeClass from events").getResults();
        for (String[] result : results) {
            CheckBox checkBox = new CheckBox(getActivity());
            checkBox.setText(getTypeSimpleText(result[0]));
            checkBox.setChecked(true);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    try {
                        initTable(workTimeTracerOpenHelper.getEventDao());
                    } catch (SQLException e) {
                        Log.e(TAG, "Could not init table, because of " + e.getMessage(), e);
                    }
                }
            });
            layout.addView(checkBox, 0);
            types.put(result[0], checkBox);
        }
    }

    private String getTypeSimpleText(String text) {
        return text.replaceAll(".*\\.", "");
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
