package com.example.fantasyf1;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

// from /picked_teams
public class Team {

    public ArrayList<Player> players;

    public String name;
    public int wildcardID;
    public int turboID;
    public int megaID;

    public Double score;
    public int totalWeeklySubs;
    public int remainingWeeklySubs;
    public double value;
    public double budget;

    public Team(JSONObject obj) {
        // parse and set fields
        players = new ArrayList<>();

        try {
            name = obj.getString("name");
            wildcardID = obj.getInt("wildcard_selected_id");
            turboID = obj.getInt("boosted_player_id");
            megaID = obj.getInt("mega_boosted_player_id");

            score = obj.getDouble("score");
            totalWeeklySubs = obj.getInt("total_num_weekly_subs");
            remainingWeeklySubs = obj.getInt("num_weekly_subs_remaining");
            value = obj.getDouble("team_value");
            budget = obj.getDouble("total_budget");
        } catch (Exception e) { e.printStackTrace(); }
    }

    // overload for team players
    public Team(JSONObject obj, ArrayList<Player> members) {
        // parse and set fields
        players = members;

        try {
            name = obj.getString("name");
            wildcardID = obj.getInt("wildcard_selected_id");
            turboID = obj.getInt("boosted_player_id");
            megaID = obj.getInt("mega_boosted_player_id");

            score = obj.getDouble("score");
            totalWeeklySubs = obj.getInt("total_num_weekly_subs");
            remainingWeeklySubs = obj.getInt("num_weekly_subs_remaining");
            value = obj.getDouble("team_value");
            budget = obj.getDouble("total_budget");
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void addPlayer(Player player) {
        // add player to players list
    }

    public void removePlayer(Player player) {
        // remove player from players list
    }
    
}
