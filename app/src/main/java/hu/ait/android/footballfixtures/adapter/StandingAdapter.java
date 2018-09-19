package hu.ait.android.footballfixtures.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import hu.ait.android.footballfixtures.R;

@SuppressLint("ViewConstructor")
public class StandingAdapter extends FrameLayout {

    public StandingAdapter(Context context, int position, String teamName, int pj, int gf, int ga,
                           int dif, int pts) {
        super(context);
        View.inflate(context, R.layout.team_row, this);

        ((TextView) findViewById(R.id.tvPosition)).setText(String.valueOf(position));
        ((TextView) findViewById(R.id.tvName)).setText(teamName);
        ((TextView) findViewById(R.id.tvName)).setPaintFlags(((TextView)
                findViewById(R.id.tvName)).getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        ((TextView) findViewById(R.id.tvPj)).setText(String.valueOf(pj));
        ((TextView) findViewById(R.id.tvGf)).setText(String.valueOf(gf));
        ((TextView) findViewById(R.id.tvGa)).setText(String.valueOf(ga));
        ((TextView) findViewById(R.id.tvDif)).setText(String.valueOf(dif));
        ((TextView) findViewById(R.id.tvPts)).setText(String.valueOf(pts));
    }

}
