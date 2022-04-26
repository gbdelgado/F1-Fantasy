package com.example.fantasyf1;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

// from /picked_teams
public class Team implements Serializable {

    public ArrayList<Player> players;
    public ArrayList<PlayerTransaction> substitutions;

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

    // for transactions dont
    Player originalConstructor;


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
            substitutions = new ArrayList<PlayerTransaction>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a transaction to the substitution array. Has logic to get rid of any circular transactions.
     * <p>
     * Speical note here that any constructor transactions will not be added. Once the user is
     * finished modifying their team, we will check if there has been a change in constructor and
     * add a transaction for that change
     *
     * @param transaction
     */
    public void addPlayerTransaction(PlayerTransaction transaction, Player playerIn) {
        // we'll filter out constructor transactions later its easier this way
        if (!playerIn.isConstructor) {
            // iterate through the substitutions and see if this transaction already exists
            boolean foundCircle = false;
            for (PlayerTransaction action : substitutions) {
                // circular transaction check
                if (action.playerIDIn == transaction.playerIDOut && action.playerIDOut == transaction.playerIDIn) {
                    /**
                     * circular transaction means there was no 'real' change in the team
                     * Ex I sub yuki for kevin, the kevin for yuki,
                     * yuki is still in my team and kevin is not
                     *
                     * remove the current transaction
                     */
                    substitutions.remove(action);
                    foundCircle = true;
                    break;
                }
            }
            if (!foundCircle) {
                // if we made it here we the transaction is new
                substitutions.add(transaction);
            }
        }
        // add the player to the actual list now
        this.addPlayer(playerIn, transaction);
        System.out.println(substitutions.toString());
    }

    /**
     * Removes old player and adds new one
     *
     * @param playerIn
     * @param transaction
     * @NOTE I NEED TO UPDATE THE LOGIC FOR WHEN WE ACTUALL HAVE A NEW CREATION
     */
    private void addPlayer(Player playerIn, PlayerTransaction transaction) {
        // out with old
        players.removeIf(e -> e.id == transaction.playerIDOut);
        // in with new
        players.add(playerIn);
    }

    /**
     * Creates a transaction for the constructor if there was one
     */
    private void filterConstructorTransactions() {
        // find the constructor
        Player newConstructor = null;
        for (Player player : players) {
            if (player.isConstructor) {
                newConstructor = player;
                break;
            }
        }

        // if they are different create a transaction and add it to transaction list
        if (newConstructor.id != originalConstructor.id) {
            PlayerTransaction sub = new PlayerTransaction(newConstructor.id, originalConstructor.id);
            substitutions.add(sub);
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
     */
    public JSONObject toJSON() {
        // filter out the constructor transactions

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
            // @TODO CHANGE TO ACTUALLY HAVE SUBTITUTITONS
            picked_team.put("substitutions", new JSONArray());
            //wrap the object
            payload.put("picked_team", picked_team);
            payload.put("captcha_token", null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return payload;
    }


}
