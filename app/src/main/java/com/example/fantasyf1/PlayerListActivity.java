package com.example.fantasyf1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerListActivity extends AppCompatActivity {

    Team team;
    HashMap<Integer, Player> players;
    PlayerAdapter playerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        team = (Team) getIntent().getSerializableExtra("TEAM");
        players = (HashMap<Integer, Player>) getIntent().getSerializableExtra("PLAYERS");
        TextView textView = findViewById(R.id.header_team_name);
        textView.setText(team.name);

        setPlayerList();
    }

    /**
     * @param view
     */
    public void handleModifyClick(View view) {
        // start the create/modify team activity
        Intent intent = new Intent(this, CreateTeamActivity.class);
        intent.putExtra("TEAM", team);
        intent.putExtra("PLAYERS", players);
        this.startActivity(intent);
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