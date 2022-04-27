package com.example.fantasyf1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

// from /picked_teams
public class Team implements Serializable {

    public ArrayList<Player> players;
    public ArrayList<PlayerTransaction> substitutions;

    public int id;
    public String name;
    public String parentID = null;
    public String userID;
    public int wildcardID;
    public int turboID;
    public int megaID;
    public int gamePeriod;

    public Double points;
    public int slot;
    public Double score;
    public int totalWeeklySubs;
    public int remainingWeeklySubs;
    public double value;
    public double budget;


    public Team(JSONObject obj) {
        // parse and set fields
        try {
            id = obj.getInt("id");
            slot = obj.getInt("slot");
            name = obj.getString("name");

            // we need actual nulls to match the API payload so make sure to replace them later
            wildcardID = obj.optInt("wildcard_selected_id", -1);
            turboID = obj.optInt("boosted_player_id", -1);
            megaID = obj.optInt( "mega_boosted_player_id", -1);

            gamePeriod = obj.getInt("game_period_id");
            userID = obj.getString("user_global_id");

            score = obj.getDouble("score");
            totalWeeklySubs = obj.getInt("total_num_weekly_subs");
            remainingWeeklySubs = obj.getInt("num_weekly_subs_remaining");
            value = obj.getDouble("team_value");
            budget = obj.getDouble("total_budget");
            substitutions = new ArrayList<PlayerTransaction>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a copy of an existsing team
     *
     * @param team
     */
    public Team(Team team) {
        this.players = new ArrayList<Player>(team.players);
        this.userID = team.userID;
        this.wildcardID = team.wildcardID;
        this.turboID = team.turboID;
        this.megaID = team.megaID;
        this.gamePeriod = team.gamePeriod;

        this.points = team.points;
        this.slot = team.slot;
        this.score = team.score;
        this.totalWeeklySubs = team.totalWeeklySubs;
        this.remainingWeeklySubs = team.remainingWeeklySubs;
        this.value = team.value;
        this.budget = team.budget;
    }

    /**
     * Swaps a player, DOES NOT VERIFY IF THE PLAYER IS ALREADY IN THE TEAM CHECK BEFORE AHHHHHH >:(
     *
     * @param playerIn
     * @param playerOut
     */
    public void swapPlayer(Player playerIn, Player playerOut) {
        // out with old
        players.removeIf(e -> e.id == playerOut.id);
        // in with new
        players.add(playerIn);
    }

    /**
     * Returns true if a player is in this team
     *
     * @param player
     * @return
     */
    public boolean playerInTeam(Player player) {
        return players.stream().anyMatch(e -> e.id == player.id);
    }


    /**
     * Forms a JSON payload to match the picked_teams route for updating
     *
     * @return
     */
    public JSONObject toJSON() {
        // filter out the constructor transactions

        // outer json
        JSONObject payload = new JSONObject();
        // inner JSON object
        JSONObject picked_team = new JSONObject();
        try {
            // java is incredibly obnoxious just let me have null values >:(((((((((
            if(this.turboID == -1) {
                picked_team.put("boosted_player_id", JSONObject.NULL);
            } else {
                picked_team.put("boosted_player_id", this.turboID);
            }
            if(this.megaID == -1) {
                picked_team.put("mega_boosted_player_id", JSONObject.NULL);
                picked_team.put("mega_player_booster_selected_id", JSONObject.NULL);
            } else {
                picked_team.put("mega_boosted_player_id", this.megaID);
                picked_team.put("mega_player_booster_selected_id", this.megaID);
            }
            if(this.wildcardID == -1) {
                picked_team.put("wildcard_selected_id", JSONObject.NULL);
            } else {
                picked_team.put("wildcard_selected_id", this.wildcardID);
            }


            picked_team.put("cancel_mega_player_booster", false);
            picked_team.put("cancel_wildcard", false);
            // @TODO CHANGE we need to get the game period from api or somewhere
            picked_team.put("game_period_id", 5);
            picked_team.put("name", this.name);
            picked_team.put("parent_id", this.parentID);
            picked_team.put("slot", this.slot);
            picked_team.put("user_id", this.userID);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // transform players array into json
        JSONArray picked_players = new JSONArray();
        for (int slot = 0; slot < this.players.size(); slot++) {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // add the players and wrap the entire object
        try {
            picked_team.put("picked_players", picked_players);
            // add the subs
            JSONArray subs = new JSONArray();
            for(PlayerTransaction sub : this.substitutions) {
                subs.put(sub.toJSON());
            }
            picked_team.put("substitutions", subs);


            //wrap the object
            payload.put("picked_team", picked_team);
            payload.put("captcha_token", null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return payload;
    }

    /**
     * Computes the set difference of the players between this team and another being
     * this.players - newTeam.players
     * then makes the transaction list
     *
     * @return
     */
    public void computeTransactions(Team newTeam) {
        // compute teamA diff
        HashSet<Player> teamADiff = new HashSet<Player>(this.players);
        HashSet<Player> teamB = new HashSet<Player>(newTeam.players);
        teamADiff.removeAll(teamB);

        // compute teamBDiff
        HashSet<Player> teamBDiff = new HashSet<Player>(newTeam.players);
        HashSet<Player> teamA = new HashSet<Player>(this.players);
        teamBDiff.removeAll(teamA);

        System.out.println("----------------");
        System.out.println("TEAM A");
        for (Player p : teamA) {
            System.out.println(p.displayName);
        }
        System.out.println("---------------");
        System.out.println("TEAM B");
        for (Player p : teamB) {
            System.out.println(p.displayName);
        }
        System.out.println("---------------");
        System.out.println("TEAM A - TEAM B");
        for (Player p : teamADiff) {
            System.out.println(p.displayName);
        }
        System.out.println("---------------");
        System.out.println("TEAM B - TEAM A");
        for (Player p : teamBDiff) {
            System.out.println(p.displayName);
        }
        System.out.println("---------------");
        System.out.println(this.substitutions);

        while (teamADiff.size() > 0) {
            Player playerOut = teamADiff.iterator().next();
            Player playerIn = teamBDiff.iterator().next();
            // add transaction
            this.substitutions.add(new PlayerTransaction(playerIn.id, playerOut.id));
            // pop the players
            teamADiff.remove(playerOut);
            teamBDiff.remove(playerIn);
        }

        for (PlayerTransaction trans : this.substitutions) {
            System.out.println("Player In: " + trans.playerIDIn + " Player Out: " + trans.playerIDOut);
        }

    }

    /**
     * because java JSON is annoying and doesn't allow null values
     */
    public static int optInt(JSONObject obj, String key) throws JSONException {
        return obj.isNull(key) ? null : obj.getInt(key);
    }

    /**
     * Two teams are equal if they share the same TEAM PLAYERS ONLY
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Team) {
            Team obj = (Team) o;
            for (Player player : obj.players) {
                if (!this.playerInTeam(player)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

}
