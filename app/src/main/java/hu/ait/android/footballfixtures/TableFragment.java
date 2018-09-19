package hu.ait.android.footballfixtures;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.parceler.Parcels;

import java.util.ArrayList;

import hu.ait.android.footballfixtures.adapter.StandingAdapter;
import hu.ait.android.footballfixtures.data.Competition;
import hu.ait.android.footballfixtures.data.LeagueTable;
import hu.ait.android.footballfixtures.data.Standing;
import hu.ait.android.footballfixtures.data.Team;
import hu.ait.android.footballfixtures.network.FixturesAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TableFragment extends Fragment {

    private LinearLayout tableLinearLayout;
    private ArrayAdapter<Competition> competitionArrayAdapter;
    private Spinner spinnerCompetitions;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.getUrlBase())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_table, container,
                false);

        tableLinearLayout = rootView.findViewById(R.id.tableLinearLayout);

        final FixturesAPI fixturesAPI = retrofit.create(FixturesAPI.class);

        spinnerCompetitions = rootView.findViewById(R.id.spinnerCompetitions);
        //noinspection ConstantConditions
        competitionArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item,
                new ArrayList<Competition>());
        spinnerCompetitions.setAdapter(competitionArrayAdapter);

        // Create fake competition
        Competition fakeCompetition = new Competition();
        fakeCompetition.setCaption("Select Competition");
        competitionArrayAdapter.add(fakeCompetition);

        // Fetch competitions
        getCompetitions(fixturesAPI);

        setSpinner(rootView, fixturesAPI);

        saveSpinnerState(savedInstanceState);

        return rootView;
    }

    private void getCompetitions(FixturesAPI fixturesAPI) {
        Call<ArrayList<Competition>> callCompetitions = fixturesAPI.getCompetitions();
        callCompetitions.enqueue(new Callback<ArrayList<Competition>>() {
            @Override
            public void onResponse(Call<ArrayList<Competition>> call,
                                   Response<ArrayList<Competition>> response) {
                for (Competition competition : response.body()) {
                    if (!competition.getCaption().equals("DFB-Pokal 2017/18") &&
                            !competition.getCaption().equals("Champions League 2017/18")) {
                        competitionArrayAdapter.add(competition);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Competition>> call, Throwable t) {
                Toast.makeText(getActivity(),
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinner(final View rootView, final FixturesAPI fixturesAPI) {
        spinnerCompetitions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    // Set filter constraint to selected competition caption
                    final Competition selectedCompetition = competitionArrayAdapter.getItem(position);

                    if (selectedCompetition != null) {
                        rootView.findViewById(R.id.tableLabel).setVisibility(View.VISIBLE);
                        rootView.findViewById(R.id.tableLinearLayout).setVisibility(View.VISIBLE);

                        tableLinearLayout.removeAllViews();

                        // Fetch league table
                        getLeagueTable(selectedCompetition, fixturesAPI);
                    }
                } else {
                    rootView.findViewById(R.id.tableLabel).setVisibility(View.INVISIBLE);
                    rootView.findViewById(R.id.tableLinearLayout).setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getLeagueTable(Competition selectedCompetition, final FixturesAPI fixturesAPI) {
        String competitionUrl = selectedCompetition.getLinks().get("self").get("href");
        int competitionId = Integer.parseInt(competitionUrl.substring(competitionUrl.
                lastIndexOf('/') + 1));
        Call<LeagueTable> callLeagueTable = fixturesAPI.getLeagueTable(competitionId);
        callLeagueTable.enqueue(new Callback<LeagueTable>() {
            @Override
            public void onResponse(Call<LeagueTable> call, Response<LeagueTable> response) {
                final LeagueTable table = response.body();
                for (final Standing standing : table.getStanding()) {
                    StandingAdapter item = new StandingAdapter(getContext(),
                            standing.getPosition(), standing.getTeamName(),
                            standing.getPlayedGames(), standing.getGoals(),
                            standing.getGoalsAgainst(), standing.getGoalDifference(),
                            standing.getPoints());

                    setItemClick(table, standing, item, fixturesAPI);

                    tableLinearLayout.addView(item);
                }
            }

            @Override
            public void onFailure(Call<LeagueTable> call, Throwable t) {
                Toast.makeText(getActivity(),
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setItemClick(final LeagueTable table, final Standing standing,
                              StandingAdapter item, final FixturesAPI fixturesAPI) {
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String teamUrl = standing.getLinks().get("team").get("href");
                final int teamId = Integer.parseInt(teamUrl.substring(teamUrl.
                        lastIndexOf('/') + 1));
                Call<Team> callTeam = fixturesAPI.getTeam(teamId);
                callTeam.enqueue(new Callback<Team>() {
                    @Override
                    public void onResponse(Call<Team> call, Response<Team> response) {
                        Team team = response.body();

                        startTeamActivity(team, table, teamId);
                    }

                    @Override
                    public void onFailure(Call<Team> call, Throwable t) {
                        Toast.makeText(getContext(),
                                "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void startTeamActivity(Team team, LeagueTable table, int teamId) {
        Intent intent = new Intent(getContext(), TeamActivity.class);
        intent.putExtra("TEAM", Parcels.wrap(team));
        intent.putExtra("LEAGUETABLE", Parcels.wrap(table));
        intent.putExtra("TEAMID", Parcels.wrap(teamId));
        startActivity(intent);
    }

    private void saveSpinnerState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int ret = savedInstanceState.getInt("SPINNER", 0);
            spinnerCompetitions.setSelection(ret);
            Log.d("SPINNER", "" + ret);
        }
    }

}
