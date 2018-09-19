package hu.ait.android.footballfixtures;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Locale;

import hu.ait.android.footballfixtures.adapter.Head2headAdapter;
import hu.ait.android.footballfixtures.data.Fixture;
import hu.ait.android.footballfixtures.data.FixtureDetails;
import hu.ait.android.footballfixtures.data.LeagueTable;
import hu.ait.android.footballfixtures.data.Team;
import hu.ait.android.footballfixtures.network.FixturesAPI;
import hu.ait.android.footballfixtures.network.RestClient;
import hu.ait.android.footballfixtures.svgloader.SvgSoftwareLayerSetter;
import retrofit.RetrofitError;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class FixtureActivity extends AppCompatActivity {

    private FixtureDetails fixtureDetails;
    private LeagueTable leagueTable;
    private TextView tvMatchDay;
    private TextView tvStatus;
    private TextView tvDate;
    private TextView tvTime;
    private TextView tvHomeTeam;
    private TextView tvAwayTeam;
    private TextView tvHomeTeamWins;
    private TextView tvAwayTeamWins;
    private TextView tvDraws;
    private ListView lvHeadToHeadFixtures;
    private ImageView ivHomeTeam;
    private ImageView ivAwayTeam;

    private RequestBuilder<PictureDrawable> requestBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixture);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.getUrlBase())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final FixturesAPI fixturesAPI = retrofit.create(FixturesAPI.class);

        requestBuilder = Glide.with(this)
                .as(PictureDrawable.class)
                .transition(withCrossFade())
                .listener(new SvgSoftwareLayerSetter());
        findViews();

        // Look up view for data population

        // Get fixture from intent
        Fixture fixture = Parcels.unwrap(this.getIntent().getParcelableExtra("FIXTURE"));

        // Set action bar title to soccer season caption
        if (fixture.getCompetition() != null) {
            setTitle(fixture.getCompetition().getCaption());
        }

        // Fetch fixture detail
        getFixtureDetails(fixturesAPI, fixture);
    }

    private void findViews() {
        tvMatchDay = findViewById(R.id.tvMatchDay);
        tvStatus = findViewById(R.id.tvStatus);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvHomeTeam = findViewById(R.id.tvHomeTeam);
        tvAwayTeam = findViewById(R.id.tvAwayTeam);
        tvHomeTeamWins = findViewById(R.id.tvHomeTeamWins);
        tvAwayTeamWins = findViewById(R.id.tvAwayTeamWins);
        tvDraws = findViewById(R.id.tvDraws);
        lvHeadToHeadFixtures = findViewById(R.id.lvHeadToHeadFixtures);
        ivHomeTeam = findViewById(R.id.ivHomeTeam);
        ivAwayTeam = findViewById(R.id.ivAwayTeam);
    }

    private void getFixtureDetails(final FixturesAPI fixturesAPI, Fixture fixture) {
        String fixtureUrl = fixture.getLinks().get("self").get("href");
        int fixtureId = Integer.parseInt(fixtureUrl.substring(fixtureUrl
                .lastIndexOf('/') + 1));
        new RestClient(this).getFootballDataService().getFixtureDetails(fixtureId,
                new retrofit.Callback<FixtureDetails>() {
                    @Override
                    public void success(FixtureDetails fixtureDetail,
                                        retrofit.client.Response response) {
                        fixtureDetails = fixtureDetail;

                        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd",
                                Locale.ENGLISH);
                        SimpleDateFormat tf = new SimpleDateFormat("hh:mm a",
                                Locale.ENGLISH);

                        // Populate the data into the template view using the data object
                        populateViews(df, tf);

                        // Fetch home team
                        getHomeTeam(fixturesAPI);

                        // Fetch away team
                        getAwayTeam(fixturesAPI);

                        // Fetch league table
                        getLeagueTable(fixturesAPI);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(FixtureActivity.this,
                                "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void populateViews(SimpleDateFormat df, SimpleDateFormat tf) {
        tvMatchDay.setText(String.format(Locale.ENGLISH, "Matchday: %d",
                fixtureDetails.getFixture().getMatchday()));
        tvStatus.setText(fixtureDetails.getFixture().getStatus());
        tvDate.setText(df.format(fixtureDetails.getFixture().getDate()));
        tvTime.setText(tf.format(fixtureDetails.getFixture().getDate()));
        tvHomeTeam.setText(fixtureDetails.getFixture().getHomeTeamName());
        tvHomeTeam.setPaintFlags(tvHomeTeam.getPaintFlags() |
                Paint.UNDERLINE_TEXT_FLAG);
        tvAwayTeam.setText(fixtureDetails.getFixture().getAwayTeamName());
        tvAwayTeam.setPaintFlags(tvAwayTeam.getPaintFlags() |
                Paint.UNDERLINE_TEXT_FLAG);
        tvHomeTeamWins.setText(String.format(Locale.ENGLISH, "%s: %d",
                fixtureDetails.getFixture().getHomeTeamName(),
                fixtureDetails.getHead2head().getHomeTeamWins()));
        tvAwayTeamWins.setText(String.format(Locale.ENGLISH, "%s: %d",
                fixtureDetails.getFixture().getAwayTeamName(),
                fixtureDetails.getHead2head().getAwayTeamWins()));
        tvDraws.setText(String.format("Draws: %s",
                fixtureDetails.getHead2head().getDraws()));
        lvHeadToHeadFixtures.setAdapter(new Head2headAdapter(getApplicationContext(),
                fixtureDetails.getHead2head().getFixtures()));
    }

    private void getHomeTeam(FixturesAPI fixturesAPI) {
        String homeTeamUrl = fixtureDetails.getFixture().getLinks().get("homeTeam").get("href");
        final int homeTeamId = Integer.parseInt(homeTeamUrl.substring(homeTeamUrl
                .lastIndexOf('/') + 1));

        Call<Team> callHomeTeam = fixturesAPI.getTeam(homeTeamId);
        callHomeTeam.enqueue(new Callback<Team>() {
            @Override
            public void onResponse(Call<Team> call, Response<Team> response) {
                final Team team = response.body();

                fixtureDetails.getFixture().setHomeTeam(team);
                setCrest(team, team.getCrestUrl(), ivHomeTeam);

                tvHomeTeam.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startTeamActivity(team, homeTeamId);
                    }
                });
            }

            @Override
            public void onFailure(Call<Team> call, Throwable t) {
                Toast.makeText(FixtureActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAwayTeam(FixturesAPI fixturesAPI) {
        String awayTeamUrl = fixtureDetails.getFixture().getLinks().get("awayTeam").get("href");
        final int awayTeamId = Integer.parseInt(awayTeamUrl.substring(awayTeamUrl
                .lastIndexOf('/') + 1));

        Call<Team> callAwayTeam = fixturesAPI.getTeam(awayTeamId);
        callAwayTeam.enqueue(new Callback<Team>() {
            @Override
            public void onResponse(Call<Team> call, Response<Team> response) {
                final Team team = response.body();

                fixtureDetails.getFixture().setAwayTeam(team);
                setCrest(team, team.getCrestUrl(), ivAwayTeam);

                tvAwayTeam.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startTeamActivity(team, awayTeamId);
                    }
                });
            }

            @Override
            public void onFailure(Call<Team> call, Throwable t) {
                Toast.makeText(FixtureActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCrest(Team team, String crestUrl, ImageView mTeamImage) {
        if (team.getCrestUrl() != null) {
            Uri uri = Uri.parse(crestUrl);
            requestBuilder.load(uri).into(mTeamImage);
        }
    }

    private void startTeamActivity(Team team, int teamId) {
        Intent intent = new Intent(getApplicationContext(), TeamActivity.class);
        intent.putExtra("TEAM", Parcels.wrap(team));
        intent.putExtra("LEAGUETABLE", Parcels.wrap(leagueTable));
        intent.putExtra("TEAMID", Parcels.wrap(teamId));
        startActivity(intent);
    }

    private void getLeagueTable(FixturesAPI fixturesAPI) {
        String competitionUrl = fixtureDetails.getFixture().getLinks()
                .get("soccerseason").get("href");
        int competitionId = Integer.parseInt(competitionUrl.substring(competitionUrl
                .lastIndexOf('/') + 1));

        Call<LeagueTable> callLeagueTable = fixturesAPI.getLeagueTable(competitionId);
        callLeagueTable.enqueue(new Callback<LeagueTable>() {
            @Override
            public void onResponse(Call<LeagueTable> call, Response<LeagueTable> response) {
                leagueTable = response.body();
            }

            @Override
            public void onFailure(Call<LeagueTable> call, Throwable t) {
                Toast.makeText(FixtureActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fixture, menu);

        // Return true to display menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_share:
                if (fixtureDetails != null) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd",
                            Locale.ENGLISH);
                    SimpleDateFormat tf = new SimpleDateFormat("hh:mm a",
                            Locale.ENGLISH);

                    // Build share text
                    StringBuilder sb = buildShareText(df, tf);

                    // Create share intent
                    createShareIntent(sb);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    private StringBuilder buildShareText(SimpleDateFormat df, SimpleDateFormat tf) {
        StringBuilder sb = new StringBuilder();

        if (fixtureDetails.getFixture().getStatus().equals("FINISHED")) {
            sb.append(String.format(Locale.ENGLISH, "%s (%d) vs. %s (%d) ",
                    fixtureDetails.getFixture().getHomeTeamName(),
                    fixtureDetails.getFixture().getResult().get("goalsHomeTeam"),
                    fixtureDetails.getFixture().getAwayTeamName(),
                    fixtureDetails.getFixture().getResult().get("goalsAwayTeam")));
        } else {
            sb.append(String.format("%s vs. %s ",
                    fixtureDetails.getFixture().getHomeTeamName(),
                    fixtureDetails.getFixture().getAwayTeamName()));
        }

        sb.append(String.format("(%s @ %s) ", df.format(fixtureDetails.getFixture().getDate()),
                tf.format(fixtureDetails.getFixture().getDate())));
        sb.append("\n");
        return sb;
    }

    private void createShareIntent(StringBuilder sb) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share fixture to..."));
    }

}
