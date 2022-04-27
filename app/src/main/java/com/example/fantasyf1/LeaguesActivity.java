package com.example.fantasyf1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LeaguesActivity extends AppCompatActivity implements APICallback {

    @Override
    public void onFinish(JSONObject response, FantasyManager.ResponseType respType, int statusCode) {
        System.out.println("JSON OUT");
        System.out.println(response.toString());

        jsonResponses.put(respType.toString().toLowerCase(Locale.ROOT), response);

        switch (respType) {
            case LEAGUE_ENTRANTS:
                parseLeagues();
                setLeaguesList();
                break;
            case GET_LEAGUE:
                setLeaderboard();
                Bundle bundle = new Bundle();
                bundle.putSerializable("LEAGUE", leagues.get(calledLeagueIndex));

                leaderboardFragment = new LeaderboardFragment();
                leaderboardFragment.setArguments(bundle);
                leaderboardFragment.setContainerActivity(this);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_leagues_page, leaderboardFragment)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }

    FantasyManager manager;
    LeaderboardFragment leaderboardFragment;

    HashMap<String, JSONObject> jsonResponses = new HashMap<>();
    ArrayList<League> leagues = new ArrayList<>();
    LeagueAdapter leagueAdapter;
    int calledLeagueIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leagues);

        manager = new FantasyManager();
        manager.getLeagueEntrants(this::onFinish);

    }

    public void onClickHandler(View view) {
        switch (view.getId()) {
            case R.id.row_league:
                ListView listView = (ListView) view.getParent();

                // get League obj for selected league
                League tempLeague = leagues.get(listView.getPositionForView(view));
                calledLeagueIndex = listView.getPositionForView(view);

                manager.getLeague(this::onFinish, tempLeague.id);
                break;
        }
    }

    private void parseLeagues() {
        try {
            JSONArray leaguesArr = jsonResponses.get("league_entrants")
                    .getJSONArray("league_entrants");

            for (int i = 0; i < leaguesArr.length(); i++) {
                JSONObject jsonLeague = (JSONObject) leaguesArr.get(i);
                League league = new League(jsonLeague);
                leagues.add(league);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setLeaguesList() {
        ListView listView = findViewById(R.id.listview_leagues_homepage);
        leagueAdapter = new LeagueAdapter(this, R.layout.row_league, leagues);
        listView.setAdapter(leagueAdapter);
    }

    private void setLeaderboard() {
        League league = leagues.get(calledLeagueIndex);

        JSONArray leaderboard = null;
        try {
            leaderboard = jsonResponses.get("get_league")
                    .getJSONObject("leaderboard")
                    .getJSONArray("leaderboard_entrants");
        } catch (Exception e) { e.printStackTrace(); }

        league.buildEntrantList(leaderboard);
    }
}