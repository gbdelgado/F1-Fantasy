/**
 * PlayerTransaction.java
 * This class represents a player swap/addition to a team. This is really only here because the API
 * requires us to send a list of players that were swapped in and out whenever we make a modification
 * this provides an easy interface that we can use for all players across all teams. An instance
 * of this class should exist in each instance of Team
 */
package com.example.fantasyf1;

import org.json.JSONObject;

public class PlayerTransaction {
    public int playerIDIn;
    public int playerIDOut;

    public PlayerTransaction(int idIn, int idOut) {
        this.playerIDIn = idIn;
        this.playerIDOut = idOut;
    }

    /**
     * Overriding equals. Two player transactions will be equal if they
     * share the same playerIn and playerOut
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof PlayerTransaction) {
            PlayerTransaction obj = (PlayerTransaction) o;
            return obj.playerIDIn == this.playerIDIn && obj.playerIDOut == this.playerIDOut;
        }
        return false;
    }

    /**
     * Returns object represetnation in JSON for the update/create request
     * @return
     */
    public JSONObject toJSON() {
        try {
            JSONObject transaction = new JSONObject();
            transaction.put("player_in_id", String.valueOf(this.playerIDIn));
            transaction.put("player_out_id", String.valueOf(this.playerIDOut));
            return transaction;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}