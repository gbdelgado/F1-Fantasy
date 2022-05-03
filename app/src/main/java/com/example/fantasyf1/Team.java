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
    public static final int NO_VALUE = -9999;
    public static final int MAX_COST = 100;
    public static final int MAX_TEAM_SIZE = 6;

    public ArrayList<Player> players;
    public ArrayList<PlayerTransaction> substitutions;

    public int id;
    public String name;
    public String parentID;
    public String userID;
    public String globalID;
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
    public double cashBalance;


    public Team(JSONObject obj) {
        // parse and set fields
        try {
            id = obj.getInt("id");
            slot = obj.getInt("slot");
            name = obj.getString("name");

            // we need actual nulls to match the API payload so make sure to replace them later
            globalID = obj.getString("global_id");
            wildcardID = obj.optInt("wildcard_selected_id", NO_VALUE);
            turboID = obj.optInt("boosted_player_id", NO_VALUE);
            megaID = obj.optInt("mega_boosted_player_id", NO_VALUE);

            gamePeriod = obj.getInt("game_period_id");
            userID = obj.getString("user_global_id");
            parentID = obj.getString("global_id");

            score = obj.getDouble("score");
            totalWeeklySubs = obj.getInt("total_num_weekly_subs");
            remainingWeeklySubs = obj.getInt("num_weekly_subs_remaining");
            value = obj.getDouble("team_value");
            cashBalance = obj.getDouble("cash_balance");
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
        this.substitutions = new ArrayList<PlayerTransaction>(team.substitutions);
        this.userID = team.userID;
        this.wildcardID = team.wildcardID;
        this.turboID = team.turboID;
        this.megaID = team.megaID;
        this.gamePeriod = team.gamePeriod;
        this.name = team.name;
        this.parentID = team.parentID;
        this.points = team.points;
        this.slot = team.slot;
        this.score = team.score;
        this.totalWeeklySubs = team.totalWeeklySubs;
        this.remainingWeeklySubs = team.remainingWeeklySubs;
        this.value = team.value;
        this.cashBalance = team.cashBalance;
    }

    /**
     * Creates an empty team with a name and a user ID and slot
     * This is for creating new team
     *
     * @param teamName
     */
    public Team(String teamName, String userID, int slot) {
        this.players = new ArrayList<Player>();
        this.substitutions = new ArrayList<PlayerTransaction>();
        this.userID = userID;
        this.wildcardID = NO_VALUE;
        this.turboID = NO_VALUE;
        this.megaID = NO_VALUE;
        //@TODO GET GAME PERIOD
        this.gamePeriod = 5;
        this.name = teamName;

        // @NOTE all this stuff gets reset when we make an api call just needde for boilerplate
        this.points = (double) NO_VALUE;
        this.slot = slot;
        this.score = (double) NO_VALUE;
        this.totalWeeklySubs = NO_VALUE;
        this.remainingWeeklySubs = 6;
        this.value = 0;
        this.cashBalance = 100;
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
        computeBudget();
    }

    /**
     * Adds a player to the player list if there is room
     *
     * @param playerIn
     */
    public void addPlayer(Player playerIn) {
        if (players.size() < MAX_TEAM_SIZE) {
            players.add(playerIn);
            computeBudget();
        }
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
     * @NOTE wierd but all of the keys in the payload ARE STRINGS EXCEPT FOR SLOT
     * this differes from the format of the response, super cool why would they be consistent  :shrug:
     */
    public JSONObject toJSON() {
        // filter out the constructor transactions

        // outer json
        JSONObject payload = new JSONObject();
        // inner JSON object
        JSONObject picked_team = new JSONObject();
        try {
            picked_team.put("parent_id", this.parentID == null ? JSONObject.NULL : parentID);
            picked_team.put("slot", this.slot);
            picked_team.put("name", this.name);
            picked_team.put("game_period_id", 5);
            picked_team.put("user_id", this.userID);

            // java is incredibly obnoxious just let me have null values >:(((((((((
            if (this.turboID == NO_VALUE) {
                picked_team.put("boosted_player_id", JSONObject.NULL);
            } else {
                picked_team.put("boosted_player_id", this.turboID);
            }
            if (this.megaID == NO_VALUE) {
                picked_team.put("mega_boosted_player_id", JSONObject.NULL);
                picked_team.put("mega_player_booster_selected_id", JSONObject.NULL);
            } else {
                picked_team.put("mega_boosted_player_id", this.megaID);
                picked_team.put("mega_player_booster_selected_id", this.megaID);
            }

            if (this.wildcardID == NO_VALUE) {
                picked_team.put("wildcard_selected_id", JSONObject.NULL);
            } else {
                picked_team.put("wildcard_selected_id", this.wildcardID);
            }

            // add the subs
            JSONArray subs = new JSONArray();
            for (PlayerTransaction sub : this.substitutions) {
                subs.put(sub.toJSON());
            }
            picked_team.put("substitutions", subs);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // transform players array into json
        JSONArray picked_players = new JSONArray();
        int currDriverSlot = 1;
        for (Player player : this.players) {
            JSONObject picked_player = new JSONObject();
            try {
                // position_id == if they are constructor or driver
                int position_id = player.isConstructor ? 2 : 1;
                // constructor slots are always 1
                int realSlot = player.isConstructor ? 1 : currDriverSlot;

                // slots are 1-indexed
                picked_player.put("player_id", String.valueOf(player.id));
                picked_player.put("position_id", String.valueOf(position_id));
                picked_player.put("slot", String.valueOf(realSlot));

                // add it to the picked_players array
                picked_players.put(picked_player);
                // only increment if this was a driver
                if (!player.isConstructor) {
                    currDriverSlot++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // add the players and wrap the entire object
        try {
            picked_team.put("picked_players", picked_players);
            picked_team.put("cancel_mega_player_booster", false);
            picked_team.put("cancel_wildcard", false);

            //wrap the object
            payload.put("picked_team", picked_team);
            payload.put("captcha_token", JSONObject.NULL);
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
    public void computeTransactions(Team newTeam) throws IllegalArgumentException {
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

        //special check if they have exceeded their subs amount
        if (teamADiff.size() > this.remainingWeeklySubs) {
            throw new IllegalArgumentException("Subs exceeded");
        }

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
        //copy the players to this player
        this.players = newTeam.players;
    }

    /**
     * Returns true if the team is over the $100m budget
     *
     * @return
     */
    public boolean isOverBudget() {
        return this.cashBalance < 0;
    }

    /**
     * returns true if this is a full team
     *
     * @return
     */
    public boolean isFullTeam() {
        return this.players.size() == MAX_TEAM_SIZE;
    }

    /**
     * returns true if the team has a turbo driver
     *
     * @return
     */
    public boolean hasTurboDriver() {
        return this.turboID != NO_VALUE;
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
            // check player equality
            for (Player player : obj.players) {
                if (!this.playerInTeam(player)) {
                    return false;
                }
            }
            // check turbo equality
            if (this.turboID != obj.turboID) {
                return false;
            }
            return true;
        }

        return false;
    }

    /**
     * Computes the budget and value fields, THIS IS CALLED BY SWAP/ADD PLAYER METHODS
     */
    private void computeBudget() {
        // compute sums
        double currPrice = 0;
        for (Player player : players) {
            currPrice += player.price;
        }

        // set fields
        this.cashBalance = MAX_COST - currPrice;
        this.value = currPrice;
    }
}
