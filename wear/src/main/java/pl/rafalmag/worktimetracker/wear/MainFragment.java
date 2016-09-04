package pl.rafalmag.worktimetracker.wear;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import test.android.rafalmag.pl.worktimetracker.R;

public class MainFragment extends Fragment {

    @BindView(R.id.mainText)
    TextView mainText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main, container, false);
        ButterKnife.bind(this, view);

        mainText.setText("Over hours\n" +
                "4h 50m\n" +
                "Start 08:40\n" +
                "Stop 18:00\n" +
                "Diff:9h15min");

        return view;
    }
}
