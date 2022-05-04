/**
 * @file:           LeaderboardFragment.java
 * @author:         CJ Larsen
 * @description:    this fragment displays a ListView of all the players within the league
 */

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

    /**
     * gets the league object passed in, the populates the ListView using custom ArrayAdapter class
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return inflated view
     */
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