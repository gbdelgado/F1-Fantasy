package com.example.fantasyf1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class HomepageActivity extends AppCompatActivity implements APICallback {

    @Override
    public void onFinish(JSONObject response, FantasyManager.ResponseType respType, int statusCode) {
        // bad
        if (statusCode > 400) {
            Toast.makeText(this, "Error Retrieving " + respType.toString(), Toast.LENGTH_SHORT).show();
        }

        System.out.println("JSON OUT");
        System.out.println(response.toString());

        jsonResponses.put(respType.toString().toLowerCase(Locale.ROOT), response);

        switch (respType) {
            case PLAYERS:
                parsePlayers();
                break;
            case PICKED_TEAMS:
                parsePickedTeams();
                break;
            case USER:
                parseUser();
                setTeamList();
                // a user can have a max of 3 teams
                System.out.println(teams.size());
                Button createTeamButton = findViewById(R.id.button_create_team);
                if (teams.size() < 3) {
                    createTeamButton.setEnabled(true);
                } else {
                    createTeamButton.setEnabled(false);
                }
                Button leaguesButton = findViewById(R.id.button_user_leagues);
                if (teams.size() > 0) {
                    leaguesButton.setEnabled(true);
                } else {
                    leaguesButton.setEnabled(false);
                }
                setLoading(false);
                break;
        }
    }

    private String userID = null;

    FantasyManager manager = new FantasyManager();
    SettingsFragment settingsFragment;
    HelpFragment helpFragment;


    HashMap<String, JSONObject> jsonResponses = new HashMap<>();
    HashMap<Integer, Player> players = new HashMap<>();
    HashMap<Integer, Team> teams = new HashMap<>();
    TeamAdapter teamAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager.getPlayers(this::onFinish);
        manager.getPickedTeams(this::onFinish, 5);
        manager.getUser(this::onFinish);

        setContentView(R.layout.activity_homepage);
        setLoading(true);
    }

    /**
     * on click handler function for any on click event in HomepageActivity. performs appropriate
     * actions depending on what onClick fired
     *
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
            case R.id.text_help:
                helpFragment = new HelpFragment();
                helpFragment.setContainerActivity(this);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_home_page, helpFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.button_user_leagues:
                Intent anotherIntent = new Intent(this, LeaguesActivity.class);
                this.startActivity(anotherIntent);
                break;
            case R.id.button_create_team:
                Intent anotherAnotherIntent = new Intent(this, CreateTeamActivity.class);
                // set create mode active
                anotherAnotherIntent.putExtra("CREATE_MODE", true);
                anotherAnotherIntent.putExtra("PLAYERS", players);
                // only needs to be set when create mode is enabled
                anotherAnotherIntent.putExtra("SLOT", teams.values().size() + 1);
                anotherAnotherIntent.putExtra("USER_ID", userID);
                this.startActivity(anotherAnotherIntent);
                break;
            default:
                System.out.println("");
        }
    }

    /**
     * onClick handler for settingsFragment
     *
     * @param view
     */
    public void settingsFragmentHandler(View view) {
        settingsFragment.toggleTheme();

        getSupportFragmentManager()
                .beginTransaction()
                .remove(settingsFragment)
                .commit();
    }

    public void helpFragmentHandler(View view) {
        helpFragment.onClickHandler(view);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    Player player = new Player(players.get(jsonPlayer.getInt("player_id")));

                    if (player.id == team.turboID) {
                        player.turbo = true;
                    }

                    tempList.add(player);
                }

                team.players = tempList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * parses user data to get point totals for each user team
     */
    private void parseUser() {
        try {
            JSONObject pickedTeams = jsonResponses.get("user")
                    .getJSONObject("user")
                    .getJSONObject("picked_team_score_totals");

            // also set the user id here, if a user has no teams this is the only way to get it
            if (teams.size() < 3) {
                String user_id = jsonResponses.get("user")
                        .getString("global_id");
                userID = user_id;
            }

            for (int i = 0; i < teams.size(); i++) {
                teams.get(i + 1).points = pickedTeams.getDouble("slot_" + (i + 1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * create ListView for the user's teams
     */
    private void setTeamList() {
        ListView listView = findViewById(R.id.listview_teams_homepage);

        teamAdapter = new TeamAdapter(this, R.layout.row_team, new ArrayList<>(teams.values()));
        listView.setAdapter(teamAdapter);
    }

    private void setLoading(boolean loading) {
        ProgressBar bar = findViewById(R.id.loading_bar);
        if (loading) {
            bar.setVisibility(View.VISIBLE);
        } else {
            bar.setVisibility(View.INVISIBLE);
        }
    }

}