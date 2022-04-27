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
import android.widget.ToggleButton;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreateTeamActivity extends AppCompatActivity implements APICallback {
    public void onFinish(JSONObject response, FantasyManager.ResponseType respType, int statusCode) {
        // bad
        if (statusCode > 400) {
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

    // @NOTE THESE WILL ONLY BE SET IF WE ARE CREATING A NEW TEAM AHHHHHHHH
    private String userID;
    private int slot;
    //////////////

    private HashMap<Integer, Player> players;
    // this helps us when we call the api, they are two different methods for updating and creating :/
    private boolean createMode;

    // slots for drivers, they are always top
    private final int[][] DRIVER_SLOTS = {
            {R.id.player_slot_1, R.id.player_slot_1_text, R.id.turbo_slot_1},
            {R.id.player_slot_2, R.id.player_slot_2_text, R.id.turbo_slot_2},
            {R.id.player_slot_3, R.id.player_slot_3_text, R.id.turbo_slot_3},
            {R.id.player_slot_4, R.id.player_slot_4_text, R.id.turbo_slot_4},
            {R.id.player_slot_5, R.id.player_slot_5_text, R.id.turbo_slot_5}
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
        // load the mode extra first
        createMode = (boolean) getIntent().getSerializableExtra("CREATE_MODE");
        players = (HashMap<Integer, Player>) getIntent().getSerializableExtra("PLAYERS");

        setContentView(R.layout.activity_create_team);
        // remote team will only be loaded from activity IF NOT IN CREATE MODE
        if (createMode) {
            // get user id from activity only in create mode, we only need it to construct
            slot = (int) getIntent().getSerializableExtra("SLOT");
            TeamNameFragment teamNameFrag = new TeamNameFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.pick_player_fragment_layout, teamNameFrag)
                    .addToBackStack(null)
                    .commit();
        } else {
            remoteTeam = (Team) getIntent().getSerializableExtra("TEAM");
            // create a local team copy based off the remote (api) version of the team
            newTeam = new Team(remoteTeam);

            // prep the frag
            Bundle bundle = new Bundle();
            // all fragments will edit the LOCAL TEAM ONLY
            bundle.putSerializable("TEAM", newTeam);
            bundle.putSerializable("PLAYERS", this.players);
            bundle.putSerializable("CREATE_MODE", createMode);
            CreateTeamFragment frag = new CreateTeamFragment();
            frag.setArguments(bundle);
            frag.setContainerActivity(this);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.pick_player_fragment_layout, frag)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Event listener for whenever a user sets a team name, if team name is null we should just
     * return to the main Activity
     */
    public void setTeamName(String teamName) {
        // create local and remote team
        if (teamName == null) {
            // if they didnt enter team name just load the main activity
            Intent intent = new Intent(this, HomepageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            // otherwise create all the goods
            remoteTeam = new Team(teamName, userID, slot);
            newTeam = new Team(remoteTeam);
            // start the fragment
            // prep the frag
            Bundle bundle = new Bundle();
            // all fragments will edit the LOCAL TEAM ONLY
            bundle.putSerializable("TEAM", newTeam);
            bundle.putSerializable("PLAYERS", this.players);
            bundle.putSerializable("CREATE_MODE", createMode);

            CreateTeamFragment frag = new CreateTeamFragment();
            frag.setArguments(bundle);
            frag.setContainerActivity(this);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.pick_player_fragment_layout, frag)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Event listener for whenever the team is changed. It will enable the continue button if the
     * original team differs from the new
     * <p>
     * This also checks the budget of the team
     */
    public void checkTeamEquality() {
        // first set if we have a change
        Button continueButton = findViewById(R.id.button_continue);
        // allow a modification if the team has change AND they are under budget
        if (!remoteTeam.equals(newTeam) && !newTeam.isOverBudget() && newTeam.isFullTeam()) {
            continueButton.setEnabled(true);
        } else {
            continueButton.setEnabled(false);
        }

        // next set the budget fields
        TextView budget = findViewById(R.id.remaining_cash_text);
        budget.setText(String.format("Remaining Cash: $%.2fM", newTeam.cashBalance));
        // set the text red if their they've exceeded the budget
        if (newTeam.isOverBudget()) {
            budget.setTextColor(getResources().getColor(R.color.red));
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
        bundle.putSerializable("CREATE_MODE", createMode);

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
        ArrayList<Player> toDisplay;
        // this will be null if the we are creating a new team and NOT making a sub
        if (playerOut == null) {
            boolean isConstructor = view.getId() == R.id.constructor_slot;
            toDisplay = (ArrayList<Player>) players.values().stream()
                    .filter(e -> {
                        return (e.isConstructor == isConstructor) && !newTeam.playerInTeam(e);
                    })
                    .collect(Collectors.toList());
        } else {
            // create a list<Player> with all the players we dont have in our team
            toDisplay = (ArrayList<Player>) players.values().stream()
                    .filter(e -> {
                        //only include players that are not the same id but the same type and not in the same team
                        return (e.isConstructor == playerOut.isConstructor) && (e.id != playerOut.id) && !newTeam.playerInTeam(e);
                    })
                    .collect(Collectors.toList());
        }

        // set the args for the frag
        Bundle bundle = new Bundle();
        bundle.putSerializable("PLAYERS", toDisplay);
        bundle.putSerializable("PLAYER_OUT", playerOut);
        bundle.putSerializable("TEAM", newTeam);
        bundle.putSerializable("CREATE_MODE", createMode);

        PlayerListFragment frag = new PlayerListFragment();
        frag.setArguments(bundle);
        frag.setContainerActivity(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.pick_player_fragment_layout, frag)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Handles the submission of a new team
     *
     * @param view
     */
    public void handleModifyTeamClick(View view) {
        // two decisions in case this is a new team
        FantasyManager manager = new FantasyManager();
        if (this.createMode) {

            manager.createTeam(this::onFinish, newTeam);
        } else {
            // process the transactions and verify that the number of subs is < their available subs left
            try {
                remoteTeam.computeTransactions(newTeam);
            } catch (IllegalArgumentException e) {
                // this means they exceeded the number of subs
                Toast.makeText(this, "Number of player substitutions exceeded", Toast.LENGTH_SHORT).show();
            }
            // make the request and reload
            manager.updateTeam(this::onFinish, remoteTeam);
        }
    }

    /**
     * Handles the change of a turbo driver
     */
    public void handleTurboChange(View view) {
        // find the turbo driver and set both visually and programmaitcily who the new turbo is
        for (int[] ids : DRIVER_SLOTS) {
            Player curr = lut.get(ids[0]);
            if(curr == null) {
                continue;
            }
            ToggleButton turboButton = findViewById(ids[2]);
            System.out.println(curr);
            // this means we have a turbo driver
            if (view.getId() == ids[2]) {
                turboButton.setChecked(true);
                // you can't unselect a turbo driver u MUST have one
                turboButton.setEnabled(false);
                // then tell the team we have a turbo
                newTeam.turboID = curr.id;
            } else if (curr.price < Player.MAX_TURBO_COST) {
                turboButton.setChecked(false);
                turboButton.setEnabled(true);
            } else {
                turboButton.setEnabled(false);
                turboButton.setChecked(false);
            }
        }

        // report that there might've been an equality change
        this.checkTeamEquality();
    }

}