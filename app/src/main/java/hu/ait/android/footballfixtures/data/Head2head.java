package hu.ait.android.footballfixtures.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class Head2head {

    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("timeFrameStart")
    @Expose
    private String timeFrameStart;
    @SerializedName("timeFrameEnd")
    @Expose
    private String timeFrameEnd;
    @SerializedName("homeTeamWins")
    @Expose
    private Integer homeTeamWins;
    @SerializedName("awayTeamWins")
    @Expose
    private Integer awayTeamWins;
    @SerializedName("draws")
    @Expose
    private Integer draws;
    @SerializedName("fixtures")
    @Expose
    private ArrayList<Fixture> fixtures;

    public Head2head() {

    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getTimeFrameStart() {
        return timeFrameStart;
    }

    public void setTimeFrameStart(String timeFrameStart) {
        this.timeFrameStart = timeFrameStart;
    }

    public String getTimeFrameEnd() {
        return timeFrameEnd;
    }

    public void setTimeFrameEnd(String timeFrameEnd) {
        this.timeFrameEnd = timeFrameEnd;
    }

    public Integer getHomeTeamWins() {
        return homeTeamWins;
    }

    public void setHomeTeamWins(Integer homeTeamWins) {
        this.homeTeamWins = homeTeamWins;
    }

    public Integer getAwayTeamWins() {
        return awayTeamWins;
    }

    public void setAwayTeamWins(Integer awayTeamWins) {
        this.awayTeamWins = awayTeamWins;
    }

    public Integer getDraws() {
        return draws;
    }

    public void setDraws(Integer draws) {
        this.draws = draws;
    }

    public ArrayList<Fixture> getFixtures() {
        return fixtures;
    }

    public void setFixtures(ArrayList<Fixture> fixtures) {
        this.fixtures = fixtures;
    }

}
