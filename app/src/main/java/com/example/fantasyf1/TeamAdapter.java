/**
 * @file:           TeamAdapter.java
 * @author:         CJ Larsen
 * @description:    this class is a custom ArrayAdapter that takes in an ArrayList of Team objects
 *                  and matches the desired info out into a view
 */

package com.example.fantasyf1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TeamAdapter extends ArrayAdapter<Team> {

    private Context context;
    private ArrayList<Team> teams;

    public TeamAdapter(Context context, int rowLayoutID, ArrayList<Team> teams) {
        super(context, rowLayoutID, teams);
        this.context = context;
        this.teams = teams;
    }

    /**
     * gets position of item in ListView, gets the corresponding Team object and populates the item
     * with info from the Team object
     * @param position
     * @param convertView
     * @param parent
     * @return inflated view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.row_team, null);
        }

        Team team = teams.get(position);

        // ImageView Here

        TextView name = (TextView) view.findViewById(R.id.row_team_name);
        name.setText(team.name);

        TextView points = (TextView) view.findViewById(R.id.row_team_total_points);
        String pointsText = "Points: " + team.points;
        points.setText(pointsText);

        TextView value = (TextView) view.findViewById(R.id.row_team_value);
        String valueText = "Value: " + team.value + "M";
        value.setText(valueText);

        return view;
    }

}
