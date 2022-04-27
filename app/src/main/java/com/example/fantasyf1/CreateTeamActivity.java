package com.example.fantasyf1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreateTeamActivity extends AppCompatActivity implements APICallback {
    public void onFinish(JSONObject response, FantasyManager.ResponseType respType, int statusCode) {
        // bad
        if(statusCode > 400) {
            Toast.makeText(this, "Error Retrieving " + respType.toString(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Team Successfully Updated", Toast.LENGTH_LONG).show();
        }
        System.out.println("RECIEVED: " + response.toString());
        // take them back to the main page so we can reload all the data
        Intent intent = new Intent(this, HomepageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    // represents the remote team thats currently in F1's api
    private Team remoteTeam;
    // represents the local team
    private Team newTeam;

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
        remoteTeam = (Team) getIntent().getSerializableExtra("TEAM");
        players = (HashMap<Integer, Player>) getIntent().getSerializableExtra("PLAYERS");

        // create a local team copy based off the remote (api) version of the team
        newTeam = new Team(remoteTeam);
        setContentView(R.layout.activity_create_team);

        // prep the frag
        Bundle bundle = new Bundle();
        // all fragments will edit the LOCAL TEAM ONLY
        bundle.putSerializable("TEAM", newTeam);
        bundle.putSerializable("PLAYERS", this.players);

        CreateTeamFragment frag = new CreateTeamFragment();
        frag.setArguments(bundle);
        frag.setContainerActivity(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.pick_player_fragment_layout, frag)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Event listener for whenever the team is changed. It will enable the continue button if the
     * original team differs from the new
     */
    public void checkTeamEquality() {
        Button continueButton = findViewById(R.id.button_continue);

        if (!remoteTeam.equals(newTeam)) {
            continueButton.setEnabled(true);
        } else {
            continueButton.setEnabled(false);
        }
    }

    /**
     * Resets the activity to undo any changes that were made
     */
    public void handleReset(View view) {
        getSupportFragmentManager().popBackStack();
        newTeam = new Team(remoteTeam);
        // prep the frag
        Bundle bundle = new Bundle();
        // all fragments will edit the LOCAL TEAM ONLY
        bundle.putSerializable("TEAM", newTeam);
        bundle.putSerializable("PLAYERS", this.players);

        CreateTeamFragment frag = new CreateTeamFragment();
        frag.setArguments(bundle);
        frag.setContainerActivity(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.pick_player_fragment_layout, frag)
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

    /**
     * Event handler for clicking on a player
     *
     * @param view
     */
    public void handleAddPlayerClick(View view) {
        // find what player we have
        Player playerOut = lut.get(view.getId());

        // create a list<Player> with all the players we dont have in our team
        ArrayList<Player> toDisplay = (ArrayList<Player>) players.values().stream()
                .filter(e -> {
                    //only include players that are not the same id but the same type and not in the same team
                    return (e.isConstructor == playerOut.isConstructor) && (e.id != playerOut.id) && !newTeam.playerInTeam(e);
                })
                .collect(Collectors.toList());

        // set the args for the frag
        Bundle bundle = new Bundle();
        bundle.putSerializable("PLAYERS", toDisplay);
        bundle.putSerializable("PLAYER_OUT", playerOut);
        bundle.putSerializable("TEAM", newTeam);

        PlayerListFragment frag = new PlayerListFragment();
        frag.setArguments(bundle);
        frag.setContainerActivity(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.pick_player_fragment_layout, frag)
                .addToBackStack(null)
                .commit();
    }

    public void handleModifyTeamClick(View view) {
        // process the transactions and verify that the number of subs is < their available subs left
        try {
            remoteTeam.computeTransactions(newTeam);
        } catch (IllegalArgumentException e) {
            // this means they exceeded the number of subs
            Toast.makeText(this, "Number of player substitutions exceeded", Toast.LENGTH_SHORT).show();
        }
        // make the request and reload
        FantasyManager manager = new FantasyManager();
        manager.updateTeam(this::onFinish, remoteTeam);
    }

}