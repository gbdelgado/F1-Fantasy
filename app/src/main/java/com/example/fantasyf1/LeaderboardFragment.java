package com.example.fantasyf1;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class LeaderboardFragment extends Fragment {

    private Activity containerActivity = null;
    private League league;

    public LeaderboardFragment() {}

    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        league = (League) getArguments().getSerializable("LEAGUE");

        ListView listView = view.findViewById(R.id.listview_leaderboard);
        EntrantAdapter entrantAdapter = new EntrantAdapter(getActivity(), R.layout.row_entrant, league.entrants);
        listView.setAdapter(entrantAdapter);

        return view;
    }

}