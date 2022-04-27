package com.example.fantasyf1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
        }
    }

    HashMap<String, JSONObject> jsonResponses = new HashMap<>();
    ArrayList<League> leagues = new ArrayList<>();
    LeagueAdapter leagueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leagues);

        FantasyManager manager = new FantasyManager();
        manager.getLeagueEntrants(this::onFinish);
//        manager.getLeagueImages(this::onFinish);

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
}