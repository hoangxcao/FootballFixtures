package hu.ait.android.footballfixtures.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hu.ait.android.footballfixtures.R;
import hu.ait.android.footballfixtures.data.Fixture;

public class Head2headAdapter extends ArrayAdapter<Fixture> {

    private List<Fixture> fixtureList;

    public Head2headAdapter(Context context, ArrayList<Fixture> fixtureList) {
        super(context, 0, fixtureList);
        this.fixtureList = fixtureList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the fixture for this position
        Fixture fixture = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.head2head_row,
                    parent, false);
        }

        // Lookup view for data population
        TextView dateTextView = convertView.findViewById(R.id.tvDate);
        TextView homeTeamGoals = convertView.findViewById(R.id.tvHomeTeamResult);
        TextView awayTeamGoals = convertView.findViewById(R.id.tvAwayTeamResult);
        populateView(fixture, dateTextView, homeTeamGoals, awayTeamGoals);


        // Return the completed view to render on screen
        return convertView;
    }

    private void populateView(Fixture fixture, TextView dateTextView, TextView homeTeamGoals,
                              TextView awayTeamGoals) {
        // Populate the data into the template view using the data object
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        if (fixture != null) {
            dateTextView.setText(df.format(fixture.getDate()));
            homeTeamGoals.setText(String.format(Locale.ENGLISH, "%s: %d",
                    fixture.getHomeTeamName(), fixture.getResult().get("goalsHomeTeam")));
            awayTeamGoals.setText(String.format(Locale.ENGLISH, "%s: %d",
                    fixture.getAwayTeamName(), fixture.getResult().get("goalsAwayTeam")));
        }
    }

}
