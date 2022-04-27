package com.example.fantasyf1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EntrantAdapter extends ArrayAdapter<League.User> {

    private Context context;
    private League.User[] leaderboard;

    public EntrantAdapter(Context context, int rowLayoutID, League.User[] leaderboard) {
        super(context, rowLayoutID, leaderboard);
        this.context = context;
        this.leaderboard = leaderboard;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.row_entrant, null);
        }

        League.User user = leaderboard[position];

        TextView rank = (TextView) view.findViewById(R.id.row_entrant_rank);
        String rankText = "" + user.rank;
        rank.setText(rankText);

        TextView teamName = (TextView) view.findViewById(R.id.row_entrant_team_name);
        teamName.setText(user.teamName);

        TextView name = (TextView) view.findViewById(R.id.row_entrant_name);
        name.setText(user.name);

        TextView points = (TextView) view.findViewById(R.id.row_entrant_score);
        String pointsText = "Points: " + user.score;
        points.setText(pointsText);

        return view;
    }

}
