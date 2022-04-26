package com.example.fantasyf1;

import org.json.JSONObject;

import java.io.Serializable;

public class League implements Serializable {

    // these are from /league_entrants
    public String name;
    public String imageURL;
    public String code;

    public int userRank;
    public int totalEntrants;
    public int usedTeamSlotNum;
    public int maxTeamLimit;

    public User[] entrants;

    public League(JSONObject json) {
        // json parsing here
    }

    public void buildEntrantList(JSONObject json) {
        // here parse json
    }

    // this is from /leagues&league_id=123456
    private class User implements Serializable {

        public String name;
        public String teamName;

        public int rank;
        public int score;
        public int teamSlot;

        public User(JSONObject json) {
            // parse json here
        }

    }

}
