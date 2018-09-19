package hu.ait.android.footballfixtures.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.ait.android.footballfixtures.R;
import hu.ait.android.footballfixtures.data.Fixture;

public class FixturesAdapter extends ArrayAdapter<Fixture> implements Filterable {

    private List<Fixture> fixtureList;

    public List<Fixture> getFilteredFixtureList() {
        return filteredFixtureList;
    }

    private List<Fixture> filteredFixtureList;
    private Filter filter;

    public FixturesAdapter(Context context, ArrayList<Fixture> fixtureList) {
        super(context, 0, fixtureList);
        this.fixtureList = fixtureList;
        this.filteredFixtureList = fixtureList;
    }

    @Override
    public int getCount() {
        return filteredFixtureList.size();
    }

    @Override
    public Fixture getItem(int position) {
        return filteredFixtureList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the fixture for this position
        Fixture fixture = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home_item,
                    parent, false);
        }

        // Lookup view for data population
        TextView teamsTextView = convertView.findViewById(R.id.tvTeams);
        TextView dateTextView = convertView.findViewById(R.id.tvDate);
        TextView competitionTextView = convertView.findViewById(R.id.tvCompetition);

        // Populate the data into the template view using the data object
        populateView(fixture, teamsTextView, dateTextView, competitionTextView);

        // Return the completed view to render on screen
        return convertView;
    }

    private void populateView(Fixture fixture, TextView teamsTextView, TextView dateTextView,
                              TextView competitionTextView) {
        if (fixture != null) {
            teamsTextView.setText(String.format("%s vs. %s", fixture.getHomeTeamName(),
                    fixture.getAwayTeamName()));
            dateTextView.setText(fixture.getDate().toString());

            if (fixture.getCompetition() != null) {
                competitionTextView.setText(fixture.getCompetition().getCaption());
            }
        }
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null && filteredFixtureList != null) {
            filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    ArrayList<Fixture> filteredFixtures = new ArrayList<>();

                    if (constraint == null || constraint.length() == 0) {
                        results.count = fixtureList.size();
                        results.values = fixtureList;
                    } else {
                        constraint = constraint.toString();
                        for (int index = 0; index < fixtureList.size(); index++) {
                            Fixture fixture = fixtureList.get(index);

                            if (fixture.getCompetition().getCaption().equals(constraint)) {
                                filteredFixtures.add(fixture);
                            }
                        }

                        results.count = filteredFixtures.size();
                        results.values = filteredFixtures;
                    }

                    return results;
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredFixtureList = (ArrayList<Fixture>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        return filter;
    }

}
