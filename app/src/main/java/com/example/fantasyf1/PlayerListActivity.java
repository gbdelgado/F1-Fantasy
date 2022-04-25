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

    String[] itemStrings = {"row_player_name", "row_player_points", "row_player_value"};
    int[] itemIds = {R.id.row_player_name, R.id.row_player_points, R.id.row_player_value};

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
        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (Player tempPlayer : team.players) {
            HashMap<String, String> map = new HashMap<>();

            map.put("row_player_name", tempPlayer.displayName);
            map.put("row_player_points", "Points: " + tempPlayer.seasonScore);
            map.put("row_player_value", "Value: $" + tempPlayer.price + "M");

            aList.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, aList, R.layout.row_player,
                itemStrings, itemIds);
        ListView listView = findViewById(R.id.listview_players_teampage);
        listView.setAdapter(simpleAdapter);
    }
}