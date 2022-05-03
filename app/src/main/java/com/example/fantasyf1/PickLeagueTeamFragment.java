package com.example.fantasyf1;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class PickLeagueTeamFragment extends Fragment {

    private Activity containerActivity;
    private TeamAdapter teamAdapter;
    private ArrayList<Team> teams;

    public PickLeagueTeamFragment() {
        // Required empty public constructor
    }

    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pick_league_team, container, false);

        teams = new ArrayList<>(((HashMap<Integer, Team>) this.getArguments().getSerializable("TEAMS")).values());

        ListView listView = view.findViewById(R.id.listview_join_league_teams);

        teamAdapter = new TeamAdapter(containerActivity, R.layout.row_team, teams);
        listView.setAdapter(teamAdapter);

        return view;
    }
}