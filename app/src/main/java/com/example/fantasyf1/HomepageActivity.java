package com.example.fantasyf1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class HomepageActivity extends AppCompatActivity implements APICallback {

    @Override
    public void onFinish(JSONObject response, FantasyManager.ResponseType respType) {
        System.out.println("JSON OUT");
        System.out.println(response.toString());

        Iterator<String> keys = response.keys();
        String route = keys.next();
        jsonResponses.put(route, response);
        if (route.equals("players")) {
            parsePlayers();
        } else if (route.equals("picked_teams")) {
            parsePickedTeams();
        }


    }

    FantasyManager manager = new FantasyManager();
    SettingsFragment settingsFragment;
    HashMap<String, JSONObject> jsonResponses = new HashMap<>();
    HashMap<Integer, Player> players = new HashMap<>();
    HashMap<Integer, Team> teams = new HashMap<>();

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
        }
    }

    /**
     * @TODO - BUG: toggling twice in a row causes loss of reference to settingsFragment
     *
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

}