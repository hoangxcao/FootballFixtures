package hu.ait.android.footballfixtures;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import hu.ait.android.footballfixtures.adapter.FixturesAdapter;
import hu.ait.android.footballfixtures.data.Competition;
import hu.ait.android.footballfixtures.data.Fixture;
import hu.ait.android.footballfixtures.data.Fixtures;
import hu.ait.android.footballfixtures.network.FixturesAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends ListFragment {

    private FixturesAdapter fixturesAdapter;
    private ArrayAdapter<Competition> competitionArrayAdapter;
    private Spinner spinnerCompetitions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.getUrlBase())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        fixturesAdapter = new FixturesAdapter(getContext(), new ArrayList<Fixture>());
        setListAdapter(fixturesAdapter);

        // Fetch fixtures
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String today = df.format(new Date());

        final FixturesAPI fixturesAPI = retrofit.create(FixturesAPI.class);

        Call<Fixtures> callFixtures = fixturesAPI.getFixtures(today, today);
        callFixtures.enqueue(new Callback<Fixtures>() {
            @Override
            public void onResponse(Call<Fixtures> call, Response<Fixtures> response) {
                for (final Fixture fixture : response.body().getFixtures()) {
                    // Get competition id from URL
                    String competitionUrl = fixture.getLinks().get("competition").get("href");
                    int competitionId = Integer.parseInt(competitionUrl.substring(competitionUrl.
                            lastIndexOf('/') + 1));

                    // Fetch fixture's competition
                    getCompetition(fixture, competitionId, fixturesAPI);
                }
            }

            @Override
            public void onFailure(Call<Fixtures> call, Throwable t) {
                Toast.makeText(getActivity(),
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        spinnerCompetitions = rootView.findViewById(R.id.spinnerCompetitions);
        //noinspection ConstantConditions
        competitionArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item,
                new ArrayList<Competition>());
        spinnerCompetitions.setAdapter(competitionArrayAdapter);

        // Create fake competition
        Competition fakeCompetition = new Competition();
        fakeCompetition.setCaption("All Competitions");
        competitionArrayAdapter.add(fakeCompetition);

        // Fetch competitions
        getCompetitions(fixturesAPI);

        setSpinner();

        saveSpinnerState(savedInstanceState);

        return rootView;
    }

    private void getCompetition(final Fixture fixture, int competitionId, FixturesAPI fixturesAPI) {
        Call<Competition> callCompetition = fixturesAPI.getCompetition(competitionId);
        callCompetition.enqueue(new Callback<Competition>() {
            @Override
            public void onResponse(Call<Competition> call,
                                   Response<Competition> response) {
                fixture.setCompetition(response.body());
                fixturesAdapter.add(fixture);
            }

            @Override
            public void onFailure(Call<Competition> call, Throwable t) {
                Toast.makeText(getActivity(),
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
//                Toast.makeText(getActivity(),
//                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinner() {
        spinnerCompetitions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String constraint = null;

                if (position != 0) {
                    // Set filter constraint to selected competition caption
                    Competition selectedCompetition = competitionArrayAdapter.getItem(position);

                    if (selectedCompetition != null) {
                        constraint = selectedCompetition.getCaption();
                    }
                }

                if (fixturesAdapter.getFilteredFixtureList() != null) {
                    fixturesAdapter.getFilter().filter(constraint);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void saveSpinnerState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int ret = savedInstanceState.getInt("SPINNER", 0);
            spinnerCompetitions.setSelection(ret);
            Log.d("SPINNER", "" + ret);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Fixture fixture = (Fixture) getListAdapter().getItem(position);

        Intent intent = new Intent(getContext(), FixtureActivity.class);
        intent.putExtra("FIXTURE", Parcels.wrap(fixture));
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SPINNER", spinnerCompetitions.getSelectedItemPosition());
    }

}
