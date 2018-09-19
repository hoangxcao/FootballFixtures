package hu.ait.android.footballfixtures.network;

import hu.ait.android.footballfixtures.data.FixtureDetails;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface Head2headAPI {

    @GET("/fixtures/{id}")
    void getFixtureDetails(@Path("id") int fixtureId, Callback<FixtureDetails> cb);

}
