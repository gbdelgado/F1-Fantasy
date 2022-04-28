package com.example.fantasyf1;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

// from /players
public class Player implements Serializable {
    // any player under this cost can be turbo
    public static final int MAX_TURBO_COST = 20;

    public int id;
    public String firstName;
    public String lastName;
    public String displayName;

    public double price;
    public double seasonScore;
    public boolean isConstructor;
    public boolean injured;
    public boolean turbo = false;

    public String teamAbbr;
    public int teamID;

    public String imageURL;

    public Player(JSONObject obj) {
        // parse and set fields
        try {
            id = obj.getInt("id");

            firstName = obj.getString("first_name");
            lastName = obj.getString("last_name");
            displayName = obj.getString("display_name");

            price = obj.getDouble("price");
            seasonScore = obj.getDouble("season_score");
            isConstructor = obj.getBoolean("is_constructor");
            injured = obj.getBoolean("injured");

            teamAbbr = obj.getString("team_abbreviation");
            teamID = obj.getInt("team_id");

            JSONObject images = (JSONObject) obj.get("headshot");
            imageURL = images.getString("profile");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Player(Player player) {
        id = player.id;

        firstName = player.firstName;
        lastName = player.lastName;
        displayName = player.displayName;

        price = player.price;
        seasonScore = player.seasonScore;
        isConstructor = player.isConstructor;
        injured = player.injured;

        teamAbbr = player.teamAbbr;
        teamID = player.teamID;

        imageURL = player.imageURL;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Player) {
            Player obj = (Player) o;
            return obj.id == this.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 69 * this.id;
    }

}
