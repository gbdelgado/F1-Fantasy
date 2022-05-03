package com.example.fantasyf1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class League implements Serializable {

    // these are from /league_entrants
    public String name;
    public String imageURL = "https://f1-backend-image-uploads-container.s3.amazonaws.com/whitelabel_multi_sport/f1/production/season_2022_1/league_image_sets/cover_image/6/F1-2022-Default-Cover-1.jpg";
    public String code;

    public int id;
    public int userRank;
    public int totalEntrants;
    public int usedTeamSlotNum;
    public int maxTeamLimit;
    // pls set this ceeg, this is the 'global_id' of the team you want to join the league
    public String picked_team_id;

    public User[] entrants;

    public League(JSONObject json) {
        try {
            userRank = json.getInt("current_overall_leaderboard_position");
            usedTeamSlotNum = json.getInt("slot");

            JSONObject details = json.getJSONObject("league");
            name = details.getString("name");
            code = details.getString("code");
            id = details.getInt("id");

            totalEntrants = details.getInt("entrants_count");
            maxTeamLimit = details.getInt("multi_entry_limit");

            // special check as some banners are null
            if (!details.isNull("background_league_image_set")) {
                imageURL = details.getJSONObject("background_league_image_set")
                        .getJSONObject("cover_image")
                        .getJSONObject("cover_image") // not a mistake i swear
                        .getString("url");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildEntrantList(JSONArray arr) {
        entrants = new User[arr.length()];

        try {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject jsonUser = (JSONObject) arr.get(i);
                User user = new User(jsonUser);
                entrants[i] = user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this is from /leagues&league_id=123456
    public class User implements Serializable {

        public String name;
        public String teamName;

        public int rank;
        public int score;
        public int teamSlot;

        public User(JSONObject json) {
            try {
                name = json.getString("username");
                teamName = json.getString("team_name");

                rank = json.getInt("rank");
                score = json.getInt("score");
                teamSlot = json.getInt("slot");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Forms a JSON payload that is sent when joining a league
     *
     * @return
     */
    public JSONObject toJSON() {
        // outerjson
        JSONObject parent = new JSONObject();
        // league entrant json where all the goods are stored
        JSONObject leagueEntrant = new JSONObject();
        try {
            leagueEntrant.put("league_id", this.id);
            leagueEntrant.put("code", this.code);
            //@TODO CJ Fill this in
            leagueEntrant.put("picked_team_id", this.picked_team_id);
            leagueEntrant.put("slot", this.usedTeamSlotNum);
            // wrap the object
            parent.put("league_entrant", leagueEntrant);
            return parent;
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Couldn't form json :(");
        }
        return null;
    }

}
