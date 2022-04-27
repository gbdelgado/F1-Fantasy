package com.example.fantasyf1;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LeagueAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<League> leagues;

    public LeagueAdapter(Context context, int rowLayoutID, ArrayList<League> leagues) {
        super(context, rowLayoutID, leagues);
        this.context = context;
        this.leagues = leagues;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.row_league, null);
        }

        League league = leagues.get(position);

        DownloadImageTask downloadImageTask = new DownloadImageTask(view.findViewById(R.id.row_league_image), league.imageURL);
        downloadImageTask.execute();

        TextView name = view.findViewById(R.id.row_league_name);
        name.setText(league.name);

        // duration here if we add it

        TextView points = view.findViewById(R.id.row_league_rank);
        String pointsText = "# " + league.userRank + " / " + league.totalEntrants;
        points.setText(pointsText);

        return view;
    }


}
