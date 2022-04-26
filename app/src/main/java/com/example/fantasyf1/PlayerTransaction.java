package com.example.fantasyf1;

import org.json.JSONObject;

/**
 *
 */
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

    public JSONObject toJSON() {
        try {
            JSONObject transaction = new JSONObject();
            transaction.put("player_in_id", this.playerIDIn);
            transaction.put("player_out_id", this.playerIDOut);
            return transaction;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}