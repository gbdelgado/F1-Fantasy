package com.example.fantasyf1;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.HashMap;

public class CreateTeamFragment extends Fragment {
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

    // current team
    private Team team;
    // lut of all the players
    private HashMap<Integer, Player> players;
    // lut of resource ID to player
    private HashMap<Integer, Player> lut;
    Activity containerActivity;

    public CreateTeamFragment() {
        // Required empty public constructor
    }

    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }

    private void fillTeam(View v) {
        // create a look up table of resource ID's to their player so we can handle on click later
        lut = new HashMap<Integer, Player>();

        int currDriverSlot = 0;
        for (Player player : team.players) {
            TextView text;
            ImageView image;
            if (player.isConstructor) {
                image = v.findViewById(CONSTRUCTOR_SLOTS[0]);
                text = v.findViewById(CONSTRUCTOR_SLOTS[1]);
                // add it to the lut
                lut.put(CONSTRUCTOR_SLOTS[0], player);
            } else {
                image = v.findViewById(DRIVER_SLOTS[currDriverSlot][0]);
                text = v.findViewById(DRIVER_SLOTS[currDriverSlot][1]);
                ToggleButton turbo = v.findViewById(DRIVER_SLOTS[currDriverSlot][2]);
                lut.put(DRIVER_SLOTS[currDriverSlot][0], player);
                // last check if this is the turbo driver
                if (team.turboID == player.id) {
                    turbo.setEnabled(false);
                    turbo.setChecked(true);
                } else if (player.price < Player.MAX_TURBO_COST) {
                    turbo.setEnabled(true);
                } else {
                    turbo.setEnabled(false);
                }
                currDriverSlot++;
            }

            // load in the image for the driver
            DownloadImageTask task = new DownloadImageTask(image, player.imageURL);
            task.execute();

            // set the text for driver/constructor
            text.setText(player.displayName);
        }
        // also set the amount of subs they have left
        TextView subs = v.findViewById(R.id.subs_left_text);
        subs.setText("Subs Left: " + team.remainingWeeklySubs);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_team, container, false);
        // load in the extras
        team = (Team) this.getArguments().getSerializable("TEAM");
        players = (HashMap<Integer, Player>) this.getArguments().getSerializable("PLAYERS");
        fillTeam(v);
        ((CreateTeamActivity) containerActivity).setLut(lut);

        return v;
    }
}