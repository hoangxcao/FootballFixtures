package hu.ait.android.footballfixtures.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Players {

    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("players")
    @Expose
    private List<Player> players;

    public Players() {

    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

}
