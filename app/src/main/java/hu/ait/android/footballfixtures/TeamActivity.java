package hu.ait.android.footballfixtures;

import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;

import org.parceler.Parcels;

import hu.ait.android.footballfixtures.adapter.PlayersAdapter;
import hu.ait.android.footballfixtures.data.LeagueTable;
import hu.ait.android.footballfixtures.data.Players;
import hu.ait.android.footballfixtures.data.Standing;
import hu.ait.android.footballfixtures.data.Team;
import hu.ait.android.footballfixtures.network.FixturesAPI;
import hu.ait.android.footballfixtures.svgloader.SvgSoftwareLayerSetter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class TeamActivity extends AppCompatActivity {

    private Team team;
    private Standing standingTeam;
    private ImageView ivTeam;
    private TextView tvMarketValue;
    private TextView tvTeamName;
    private TextView tvPosition;
    private TextView tvPj;
    private TextView tvGf;
    private TextView tvGa;
    private TextView tvDif;
    private TextView tvPts;
    private ListView lvPlayers;

    private RequestBuilder<PictureDrawable> requestBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.getUrlBase())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final FixturesAPI fixturesAPI = retrofit.create(FixturesAPI.class);

        requestBuilder = Glide.with(this)
                .as(PictureDrawable.class)
                .transition(withCrossFade())
                .listener(new SvgSoftwareLayerSetter());

        // Get team from intent
        team = Parcels.unwrap(this.getIntent().getParcelableExtra("TEAM"));
        LeagueTable leagueTable = Parcels.unwrap(this.getIntent()
                .getParcelableExtra("LEAGUETABLE"));
        int teamId = Parcels.unwrap(this.getIntent().getParcelableExtra("TEAMID"));

        // Set Action Bar title to team's name
        if (team.getName() != null) {
            //noinspection ConstantConditions
            setTitle(team.getName());
        }

        for (Standing st : leagueTable.getStanding()) {
            if (st.getTeamName().equals(team.getName())) {
                standingTeam = st;
                break;
            }
        }

        // Look up views
        findViews();

        // Populate views
        populateViews();

        setCrest();

        Call<Players> callFixtures = fixturesAPI.getPlayers(teamId);
        callFixtures.enqueue(new Callback<Players>() {
            @Override
            public void onResponse(Call<Players> call, retrofit2.Response<Players> response) {
                lvPlayers.setAdapter(new PlayersAdapter(getApplicationContext(),
                        response.body().getPlayers()));
            }

            @Override
            public void onFailure(Call<Players> call, Throwable t) {

            }
        });
    }

    private void findViews() {
        ivTeam = findViewById(R.id.ivTeam);
        tvMarketValue = findViewById(R.id.tvMarketValue);
        tvTeamName = findViewById(R.id.tvTeamName);
        tvPosition = findViewById(R.id.tvPosition);
        tvPj = findViewById(R.id.tvPj);
        tvGf = findViewById(R.id.tvGf);
        tvGa = findViewById(R.id.tvGa);
        tvDif = findViewById(R.id.tvDif);
        tvPts = findViewById(R.id.tvPts);
        lvPlayers = findViewById(R.id.lvPlayers);
    }

    private void populateViews() {
        tvMarketValue.setText(team.getSquadMarketValue());
        tvTeamName.setText(team.getName());
        tvPosition.setText(String.valueOf(standingTeam.getPosition()));
        tvPj.setText(String.valueOf(standingTeam.getPlayedGames()));
        tvGf.setText(String.valueOf(standingTeam.getGoals()));
        tvGa.setText(String.valueOf(standingTeam.getGoalsAgainst()));
        tvDif.setText(String.valueOf(standingTeam.getGoalDifference()));
        tvPts.setText(String.valueOf(standingTeam.getPoints()));
    }

    private void setCrest() {
        if (team.getCrestUrl() != null) {
            Uri uri = Uri.parse(team.getCrestUrl());
            requestBuilder.load(uri).into(ivTeam);
        }
    }

}
