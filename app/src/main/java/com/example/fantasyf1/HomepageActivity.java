package com.example.fantasyf1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HomepageActivity extends AppCompatActivity implements APICallback {

    @Override
    public void onFinish(JSONObject response, FantasyManager.ResponseType respType) {
        System.out.println("JSON OUT");
        System.out.println(response.toString());

        Iterator<String> keys = response.keys();
        String route = keys.next();
        jsonResponses.put(route, response);
        if (respType == FantasyManager.ResponseType.PLAYERS) {
            parsePlayers();
        } else if (respType == FantasyManager.ResponseType.PICKED_TEAMS) {
            parsePickedTeams();
            setTeamList();
            // a user can have a max of 3 teams
            Button createTeamButton = findViewById(R.id.create_team_button);
            if(teams.size() < 3) {
                createTeamButton.setVisibility(View.VISIBLE);
            } else {
                createTeamButton.setVisibility(View.GONE);
            }
        }


    }

    FantasyManager manager = new FantasyManager();
    SettingsFragment settingsFragment;

    HashMap<String, JSONObject> jsonResponses = new HashMap<>();
    HashMap<Integer, Player> players = new HashMap<>();
    HashMap<Integer, Team> teams = new HashMap<>();
    TeamAdapter teamAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager.getPlayers(this::onFinish);
        manager.getPickedTeams(this::onFinish, 5);

        setContentView(R.layout.activity_homepage);
    }

    /**
     * on click handler function for any on click event in HomepageActivity. performs appropriate
     * actions depending on what onClick fired
     * @param view
     */
    public void onClickHandler(View view) {
        switch (view.getId()) {
            case R.id.image_settings:
                 settingsFragment = new SettingsFragment();
                 settingsFragment.setContainerActivity(this);
                 getSupportFragmentManager().beginTransaction()
                         .replace(R.id.layout_home_page, settingsFragment)
                         .addToBackStack(null)
                         .commit();
                 break;
            case R.id.row_team:
                ListView listView = (ListView) view.getParent();
                // slots are 1-indexed
                int slot = listView.getPositionForView(view) + 1;
                Intent intent = new Intent(this, PlayerListActivity.class);
                intent.putExtra("TEAM", teams.get(slot));
                intent.putExtra("PLAYERS", players);
                this.startActivity(intent);
                break;
            default:
                System.out.println("");
        }
    }

    /**
     * onClick handler for settingsFragment
     * @param view
     */
    public void settingsFragmentHandler(View view) {
        settingsFragment.toggleTheme();

        getSupportFragmentManager()
                .beginTransaction()
                .remove(settingsFragment)
                .commit();
    }

    /**
     * runs through JSONArray of players to build HashMap of Player objects
     */
    private void parsePlayers() {
        try {
            JSONArray playerArr = jsonResponses.get("players").getJSONArray("players");
            for (int i = 0; i < playerArr.length(); i++) {
                JSONObject jsonPlayer = playerArr.getJSONObject(i);
                Player player = new Player(jsonPlayer);
                players.put(player.id, player);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * runs through JSONArray of user teams, and build ArrayList of Players on the team
     */
    private void parsePickedTeams() {
        try {
            JSONArray teamArr = jsonResponses.get("picked_teams").getJSONArray("picked_teams");
            for (int i = 0; i < teamArr.length(); i++) {
                JSONObject jsonTeam = teamArr.getJSONObject(i);
                Team team = new Team(jsonTeam);
                teams.put(team.slot, team);

                // build player list for team
                ArrayList<Player> tempList = new ArrayList<>();
                JSONArray playersArr = jsonTeam.getJSONArray("picked_players");
                for (int j = 0; j < playersArr.length(); j++) {
                    JSONObject jsonPlayer = playersArr.getJSONObject(j);
                    tempList.add(players.get(jsonPlayer.getInt("player_id")));
                }

                team.players = tempList;
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * create ListView for the user's teams
     */
    private void setTeamList() {
        ListView listView = findViewById(R.id.listview_teams_homepage);

        teamAdapter = new TeamAdapter(this, R.layout.row_team, new ArrayList<>(teams.values()));
        listView.setAdapter(teamAdapter);
    }

}