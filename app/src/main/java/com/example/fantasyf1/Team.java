package com.example.fantasyf1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

// from /picked_teams
public class Team {

    public ArrayList<Player> players;

    public String name;
    public String parentID = null;
    public String userID;
    public int wildcardID;
    public int turboID;
    public int megaID;
    public int gamePeriod;

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

            gamePeriod = obj.getInt("game_period_id");
            userID = obj.getString("user_global_id");

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

    /**
     * Forms a JSON payload to match the picked_teams route for updating
     * @return
     */
    public JSONObject toJSON() {
        // outer json
        JSONObject payload = new JSONObject();
        // inner JSON object
        JSONObject picked_team = new JSONObject();
        try {
            picked_team.put("boosted_player_id", this.turboID);
            picked_team.put("cancel_mega_player_booster", false);
            picked_team.put("cancel_wildcard", false);
            // @TODO CHANGE we need to get the game period from api or somewhere
            picked_team.put("game_period_id", 5);
            picked_team.put("mega_boosted_player_id", this.megaID);
            picked_team.put("mega_player_booster_selected_id", this.megaID);
            picked_team.put("name", this.name);
            picked_team.put("parent_id", this.parentID);
            picked_team.put("slot", this.slot);
            picked_team.put("user_id", this.userID);
            picked_team.put("wildcard_selected_id", this.wildcardID);

        } catch (Exception e) { e.printStackTrace(); }

        // transform players array into json
        JSONArray picked_players = new JSONArray();
        for(int slot = 0; slot < this.players.size(); slot++) {
            JSONObject picked_player = new JSONObject();
            try {
                // position_id == if they are constructor or driver
                int position_id = players.get(slot).isConstructor ? 2 : 1;
                // constructor slots are always 1
                int realSlot = players.get(slot).isConstructor ? 1 : slot + 1;

                // slots are 1-indexed
                picked_player.put("slot", realSlot);
                picked_player.put("player_id", this.players.get(slot).id);
                picked_player.put("position_id", position_id);

                // add it to the picked_players array
                picked_players.put(picked_player);
            } catch (Exception e) { e.printStackTrace(); }
        }

        // add the players and wrap the entire object
        try {
            picked_team.put("picked_players", picked_players);
            // @TODO CHANGE TO ACTUALLY HAVE SUBTITUTITONS
            picked_team.put("substitutions", new JSONArray());
            //wrap the object
            payload.put("picked_team", picked_team);
            payload.put("captcha_token", null);
        } catch (Exception e) { e.printStackTrace(); }

        return payload;
    }
}
