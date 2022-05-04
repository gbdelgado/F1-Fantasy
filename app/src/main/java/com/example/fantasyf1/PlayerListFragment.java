/**
 * PlayerListFragment.java
 * This fragment represents the view you see when you click on a player
 * during a team creation/modification
 *
 */
package com.example.fantasyf1;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class PlayerListFragment extends Fragment {
    Activity containerActivity = null;
    Player playerIn = null;
    PlayerAdapter playerAdapter;
    Team team;
    Player playerOut;
    boolean createMode;

    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }

    public PlayerListFragment() {
        // Required empty public constructor
    }

    /**
     * Tells the event listener that something might have changed
     */
    @Override
    public void onDetach() {
        super.onDetach();
        ((CreateTeamActivity) containerActivity).checkTeamEquality();
    }


    /**
     * Creates the view, and creates a callback for whenever a player is clicked. It will report
     * the player swap details to the parent
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_player_list, container, false);

        // get the players
        ArrayList<Player> players = (ArrayList<Player>) this.getArguments().getSerializable("PLAYERS");
        playerOut = (Player) this.getArguments().getSerializable("PLAYER_OUT");
        team = (Team) this.getArguments().getSerializable("TEAM");

        playerAdapter = new PlayerAdapter(getContext(), R.layout.row_player, players);

        ListView listView = (ListView) v.findViewById(R.id.listview_players_create_team);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // get the id of the player they clicked
                playerIn = (Player) adapterView.getItemAtPosition(i);
                if (playerOut != null) {
                    team.swapPlayer(playerIn, playerOut);
                } else {
                    team.addPlayer(playerIn);
                }

                // return to the team selection
                ((CreateTeamActivity) containerActivity).getSupportFragmentManager()
                        .popBackStack();
            }
        });
        listView.setAdapter(playerAdapter);
        return v;
    }
}