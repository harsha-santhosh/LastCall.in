package com.inoek.apps.venuser.lastcallin;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment {


    private View main_view;
    private TextView m_missed_calls;
    private TextView m_incoming_calls;
    private TextView m_outgoing_calls;
    private TextView m_incoming_dur;
    private TextView m_outgoing_dur;
    private TextView m_stats_from;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    private String getFormattedDate(String datetime)
    {
        String formattedDate = "";
        if(datetime != null && !datetime.isEmpty()) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.valueOf(datetime));
            String callDay = String.valueOf(cal.get(Calendar.DATE));
            int month = cal.get(Calendar.MONTH);
            String callMonth = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);
            String callYear = String.valueOf(cal.get(Calendar.YEAR));
            formattedDate = callDay + "-" + callMonth + "-" + callYear;
        }
        return formattedDate;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        main_view = rootView;
        m_missed_calls = (TextView) main_view.findViewById(R.id.missed_calls);
        m_incoming_calls = (TextView) main_view.findViewById(R.id.incoming_calls);
        m_outgoing_calls = (TextView) main_view.findViewById(R.id.outgoing_calls);
        m_incoming_dur = (TextView) main_view.findViewById(R.id.incoming_duration);
        m_outgoing_dur = (TextView) main_view.findViewById(R.id.outgoing_duration);
        m_stats_from = (TextView) main_view.findViewById(R.id.from_date);
        String fromDate = getFormattedDate(BaseActivity.lastDateImported);
        m_missed_calls.setText(String.valueOf(BaseActivity.mCallsMissed));
        m_incoming_calls.setText(String.valueOf(BaseActivity.mCallsIncoming));
        m_outgoing_calls.setText(String.valueOf(BaseActivity.mCallsOutgoing));
        m_incoming_dur.setText(BaseActivity.mIncomingDurationString);
        m_outgoing_dur.setText(BaseActivity.mOutgoingDurationString);
        m_stats_from.setText(fromDate);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}
