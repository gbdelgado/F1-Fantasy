package com.example.fantasyf1;

import org.json.JSONObject;
import java.util.ArrayList;

// from /picked_teams
public class Team {

    public ArrayList<Player> players;

    public String name;
    public int wildcardID;
    public int turboID;
    public int megaID;

    public int slot;
    public Double score;
    public int totalWeeklySubs;
    public int remainingWeeklySubs;
    public double value;
    public double budget;

    public Team(JSONObject obj) {
        // parse and set fields
        try {
            slot = obj.getInt("slot");
            name = obj.getString("name");
            wildcardID = obj.optInt("wildcard_selected_id", -1);
            turboID = obj.optInt("boosted_player_id", -1);
            megaID = obj.optInt("mega_boosted_player_id", -1);

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
            slot = obj.getInt("slot");
            name = obj.getString("name");
            wildcardID = obj.optInt("wildcard_selected_id", -1);
            turboID = obj.optInt("boosted_player_id", -1);
            megaID = obj.optInt("mega_boosted_player_id", -1);

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
