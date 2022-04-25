package com.example.fantasyf1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerListActivity extends AppCompatActivity {

    Team team;
    PlayerAdapter playerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        team = (Team) getIntent().getSerializableExtra("TEAM");
        TextView textView = findViewById(R.id.header_team_name);
        textView.setText(team.name);

        setPlayerList();
    }

    /**
     * create ListView for team's players
     */
    private void setPlayerList() {
        ListView listView = findViewById(R.id.listview_players_teampage);
        playerAdapter = new PlayerAdapter(this, R.layout.row_player, team.players);
        listView.setAdapter(playerAdapter);
    }
}