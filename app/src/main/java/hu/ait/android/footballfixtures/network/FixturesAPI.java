package hu.ait.android.footballfixtures.network;

import java.util.ArrayList;

import hu.ait.android.footballfixtures.data.Competition;
import hu.ait.android.footballfixtures.data.Fixtures;
import hu.ait.android.footballfixtures.data.LeagueTable;
import hu.ait.android.footballfixtures.data.Players;
import hu.ait.android.footballfixtures.data.Team;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FixturesAPI {

    @Headers("X-Auth-Token: e0f8b895ea55413c9d1e3b754e8f261b")
    @GET("/v1/fixtures")
    Call<Fixtures> getFixtures(@Query("timeFrameStart") String timeFrameStart,
                               @Query("timeFrameEnd") String timeFrameEnd);

    @Headers("X-Auth-Token: e0f8b895ea55413c9d1e3b754e8f261b")
    @GET("/v1/competitions")
    Call<ArrayList<Competition>> getCompetitions();

    @Headers("X-Auth-Token: e0f8b895ea55413c9d1e3b754e8f261b")
    @GET("/v1/competitions/{id}")
    Call<Competition> getCompetition(@Path("id") int competitionId);

    @Headers("X-Auth-Token: e0f8b895ea55413c9d1e3b754e8f261b")
    @GET("/v1/competitions/{id}/leagueTable")
    Call<LeagueTable> getLeagueTable(@Path("id") int competitionId);

    @Headers("X-Auth-Token: e0f8b895ea55413c9d1e3b754e8f261b")
    @GET("/v1/teams/{id}")
    Call<Team> getTeam(@Path("id") int teamId);

    @Headers("X-Auth-Token: e0f8b895ea55413c9d1e3b754e8f261b")
    @GET("/v1/teams/{id}/players")
    Call<Players> getPlayers(@Path("id") int teamId);

}
