package com.example.fantasyf1;

import org.json.JSONObject;

import java.io.Serializable;

public class League implements Serializable {

    // these are from /league_entrants
    public String name;
    public String imageURL = "https://f1-backend-image-uploads-container.s3.amazonaws.com/whitelabel_multi_sport/f1/production/season_2022_1/league_image_sets/cover_image/6/F1-2022-Default-Cover-1.jpg";
    public String code;
    public String id;

    public int userRank;
    public int totalEntrants;
    public int usedTeamSlotNum;
    public int maxTeamLimit;

    public User[] entrants;

    public League(JSONObject json) {
        try {
            userRank = json.getInt("current_overall_leaderboard_position");
            usedTeamSlotNum = json.getInt("slot");

            JSONObject details = json.getJSONObject("league");
            name = details.getString("name");
            code = details.getString("code");
            id = details.getString("id");

            totalEntrants = details.getInt("entrants_count");
            maxTeamLimit = details.getInt("multi_entry_limit");

            // special check as some banners are null
            if (!details.isNull("background_league_image_set")) {
                imageURL = details.getJSONObject("background_league_image_set")
                        .getJSONObject("cover_image")
                        .getJSONObject("cover_image") // not a mistake i swear
                        .getString("url");
            }

        } catch (Exception e) { e.printStackTrace(); }
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
