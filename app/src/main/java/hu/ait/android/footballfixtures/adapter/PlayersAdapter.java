package hu.ait.android.footballfixtures.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import hu.ait.android.footballfixtures.R;
import hu.ait.android.footballfixtures.data.Player;

public class PlayersAdapter extends ArrayAdapter<Player> {

    private List<Player> playerList;

    public PlayersAdapter(Context context, List<Player> playerList) {
        super(context, 0, playerList);
        this.playerList = playerList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the fixture for this position
        Player player = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.player_row, parent,
                    false);
        }

        // Lookup view for data population
        TextView jerseyNoTextView = convertView.findViewById(R.id.tvPlayerNumber);
        TextView nameTextView = convertView.findViewById(R.id.tvPlayerName);
        TextView positionTextView = convertView.findViewById(R.id.tvPlayerPosition);
        TextView nationalityTextView = convertView.findViewById(R.id.tvPlayerNationality);

        // Populate the data into the template view using the data object
        populateView(player, jerseyNoTextView, nameTextView, positionTextView, nationalityTextView);

        // Return the completed view to render on screen
        return convertView;
    }

    private void populateView(Player player, TextView jerseyNoTextView, TextView nameTextView,
                              TextView positionTextView, TextView nationalityTextView) {
        if (player != null) {
            jerseyNoTextView.setText(String.valueOf(player.getJerseyNumber()));
            nameTextView.setText(player.getName());
            positionTextView.setText(player.getPosition());
            nationalityTextView.setText(player.getNationality());
        }
    }

}
