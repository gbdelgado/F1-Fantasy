package com.example.fantasyf1;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

// from /players
public class Player implements Serializable {

    public int id;
    public String firstName;
    public String lastName;
    public String displayName;

    public double price;
    public double seasonScore;
    public boolean isConstructor;
    public boolean injured;

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

    public ArrayList<Object> getPlayerListRow() {
        // return fields needed for each player row within a team list
        return null;
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
