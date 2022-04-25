package com.example.fantasyf1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.stream.Stream;

public class CreateTeamActivity extends AppCompatActivity {
    private Team team;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // load the extras
        team = (Team) getIntent().getSerializableExtra("TEAM");
        setContentView(R.layout.activity_create_team);

        // if team is null, then we are creating a new team
        if (team == null) {
            this.createMode = true;
        } else {
            this.fillTeam();
        }

    }

    /**
     * Fills in all of the drivers and constructor
     */
    private void fillTeam() {
        // find the constructor
        int currDriverSlot = 0;
        for (Player player : team.players) {
            TextView text;
            ImageView image;
            if (player.isConstructor) {
                image = findViewById(CONSTRUCTOR_SLOTS[0]);
                text = findViewById(CONSTRUCTOR_SLOTS[1]);
            } else {
                image = findViewById(DRIVER_SLOTS[currDriverSlot][0]);
                text = findViewById(DRIVER_SLOTS[currDriverSlot][1]);
                currDriverSlot++;
            }

            // load in the image for the driver
            DownloadImageTask task = new DownloadImageTask(image, player.imageURL);
            task.execute();

            // set the text for driver/constructor
            text.setText(player.displayName);
        }

    }


    public void handleClick(View view) {

    }
}