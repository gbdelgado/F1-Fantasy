package com.example.fantasyf1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreateTeamActivity extends AppCompatActivity {
    private Team team;
    private HashMap<Integer, Player> players;
    // this helps us when we call the api, they are two different methods for updating and creating :/
    private boolean createMode;

    // slots for drivers, they are always top
    private final int[][] DRIVER_SLOTS = {
            {R.id.player_slot_1, R.id.player_slot_1_text},
            {R.id.player_slot_2, R.id.player_slot_2_text},
            {R.id.player_slot_3, R.id.player_slot_3_text},
            {R.id.player_slot_4, R.id.player_slot_4_text},
            {R.id.player_slot_5, R.id.player_slot_5_text}
    };

    // slot for constructor, always bottom
    private final int[] CONSTRUCTOR_SLOTS = {
            R.id.constructor_slot,
            R.id.constructor_slot_text
    };

    private HashMap<Integer, Player> lut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // load the extras
        team = (Team) getIntent().getSerializableExtra("TEAM");
        players = (HashMap<Integer, Player>) getIntent().getSerializableExtra("PLAYERS");
        System.out.println(players);
        setContentView(R.layout.activity_create_team);

        // prep the frag
        Bundle bundle = new Bundle();
        bundle.putSerializable("TEAM", this.team);
        bundle.putSerializable("PLAYERS", this.players);

        CreateTeamFragment frag = new CreateTeamFragment();
        frag.setArguments(bundle);
        frag.setContainerActivity(this);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.pick_player_fragment_layout, frag)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Gets the lut from the fragment
     *
     * @param lut
     */
    public void setLut(HashMap<Integer, Player> lut) {
        this.lut = lut;
    }

    public void handleAddPlayerClick(View view) {
        // find what player we have
        Player playerOut = lut.get(view.getId());

        // create a list<Player> with all the players we dont have in our team
        ArrayList<Player> toDisplay = (ArrayList<Player>) players.values().stream()
                .filter(e -> {
                    //only include players that are not the same id but the same type and not in the same team
                    return (e.isConstructor == playerOut.isConstructor) && (e.id != playerOut.id) && !team.playerInTeam(e);
                })
                .collect(Collectors.toList());

        // set the args for the frag
        Bundle bundle = new Bundle();
        bundle.putSerializable("PLAYERS", toDisplay);
        bundle.putSerializable("PLAYER_OUT", playerOut);
        bundle.putSerializable("TEAM", this.team);

        PlayerListFragment frag = new PlayerListFragment();
        frag.setArguments(bundle);
        frag.setContainerActivity(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.pick_player_fragment_layout, frag)
                .addToBackStack(null)
                .commit();
    }


}