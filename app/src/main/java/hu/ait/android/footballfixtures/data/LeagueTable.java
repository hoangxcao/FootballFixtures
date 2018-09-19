package hu.ait.android.footballfixtures.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class LeagueTable {

    @SerializedName("leagueCaption")
    @Expose
    private String leagueCaption;
    @SerializedName("matchday")
    @Expose
    private Integer matchday;
    @SerializedName("standing")
    @Expose
    private List<Standing> standing;

    public LeagueTable() {

    }

    public String getLeagueCaption() {
        return leagueCaption;
    }

    public void setLeagueCaption(String leagueCaption) {
        this.leagueCaption = leagueCaption;
    }

    public Integer getMatchday() {
        return matchday;
    }

    public void setMatchday(Integer matchday) {
        this.matchday = matchday;
    }

    public List<Standing> getStanding() {
        return standing;
    }

    public void setStanding(List<Standing> standing) {
        this.standing = standing;
    }

}
