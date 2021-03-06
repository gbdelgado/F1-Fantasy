/**
 * FantasyManager.java
 * FantasyManager provides an interface for talking the the f1 fantasy api. This class is responsible
 * for getting the special login cookies, prepping all of headers, and decoding the responses.
 * <p>
 * Each request takes in an APICallback method which will be called after the asynctask has
 * reached its' onPostExecute method. In combination with this each callback will provide
 * a ResponseType enum to let the caller know which response they are getting.
 */
package com.example.fantasyf1;

import android.os.AsyncTask;
import android.webkit.CookieManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

public class FantasyManager {
    public static final String AUTH_URL = "https://api.formula1.com/v2/account/subscriber/authenticate/by-password";

    private static enum RequestType {
        GET,
        POST,
        PUT
    }

    public static enum ResponseType {
        PICKED_TEAMS,
        USER,
        PLAYERS,
        BOOSTERS,
        SEASON,
        TEAMS,
        LEAGUE_ENTRANTS,
        LEAGUE_IMAGES,
        GET_LEAGUE,
        JOIN_LEAGUE
    }

    private boolean loggedIn;
    private HashMap<String, String> headers;

    public FantasyManager() {
        this.loggedIn = false;
        this.setDefaultHeaders();
    }

    /**
     * Generates the default headers for all the subsequent requests
     * The cookie here is super important, if we recieve a bad request well need
     * to have the user log in again so we can grab the new authentication cookie
     * <p>
     * This method sets the HashMap headers property
     */
    private void setDefaultHeaders() {
        // generate the default headers
        this.headers = new HashMap<String, String>();
        // this is the important one!!!!!!
        String[] test = {
                "https://fantasy-api.formula1.com/f1/2022/picked_teams?v=1&game_period_id=4&my_current_picked_teams=true&my_next_picked_teams=true",
                "https://fantasy-api.formula1.com/f1/2022/users?v=1&current=true"
        };
        for (String url : test) {
            String encodedCookie = CookieManager.getInstance().getCookie(url);
            System.out.println(encodedCookie);
        }

        String cookie = CookieManager.getInstance().getCookie(test[0]);


        headers.put("Cookie", cookie);
        // other stuff is standard
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Cache-Control", "no-cache");
        headers.put("Connection", "keep-alive");
        headers.put("Host", "fantasy-api.formula1.com");
        headers.put("Origin", "https://fantasy.formula1.com");
        headers.put("Pragma", "no-cache");
        headers.put("Sec-Fetch-Dest", "empty");
        headers.put("Sec-Fetch-Mode", "cors");
        headers.put("Sec-Fetch-Site", "same-site");
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36");
        headers.put("accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"100\", \"Google Chrome\";v=\"100\"");
        headers.put("sec-ch-ua-mobile", "?0");
        headers.put("sec-ch-ua-platform", "Windows");
        headers.put("x-build", "c1a1d483");
        headers.put("x-project", "flutter");
        headers.put("x-version", "2.5.3");
    }

    /**
     * Gets the users information
     * Request Type: GET
     *
     * @param callback
     */
    public void getUser(APICallback callback) {
        String api_url = "https://fantasy-api.formula1.com/f1/2022/users?v=1&current=true";
        // spin up the task
        CallAPITask task = new CallAPITask(callback, api_url, RequestType.GET, ResponseType.USER);
        task.execute();
    }

    /**
     * Method for /2022 route
     * Request Type: GET
     *
     * @param callback
     */
    public void getSeason(APICallback callback) {
        String api_url = "https://fantasy-api.formula1.com/f1/2022?v=1";
        // spin up the task
        CallAPITask task = new CallAPITask(callback, api_url, RequestType.GET, ResponseType.SEASON);
        task.execute();
    }

    /**
     * Method for /teams
     * Requst Type: GET
     *
     * @param callback
     */
    public void getTeams(APICallback callback) {
        String api_url = "https://fantasy-api.formula1.com/f1/2022/teams?v=1";
        // spin up the task
        CallAPITask task = new CallAPITask(callback, api_url, RequestType.GET, ResponseType.TEAMS);
        task.execute();
    }

    /**
     * Method for /boosters
     * Request Type: GET
     *
     * @param callback
     */
    public void getBoosters(APICallback callback) {
        String api_url = "https://fantasy-api.formula1.com/f1/2022/boosters?v=1";
        // spin up the task
        CallAPITask task = new CallAPITask(callback, api_url, RequestType.GET, ResponseType.BOOSTERS);
        task.execute();
    }

    /**
     * Method for /league_entrants
     * Request Type: GET
     *
     * @param callback
     */
    public void getLeagueEntrants(APICallback callback) {
        String api_url = "https://fantasy-api.formula1.com/f1/2022/league_entrants?v=1";
        CallAPITask task = new CallAPITask(callback, api_url, RequestType.GET, ResponseType.LEAGUE_ENTRANTS);
        task.execute();
    }

    /**
     * Method for /league_image_sets
     * Request Type: GET
     *
     * @param callback
     */
    public void getLeagueImages(APICallback callback) {
        String api_url = "https://fantasy-api.formula1.com/f1/2022/league_image_sets?v=1";
        CallAPITask task = new CallAPITask(callback, api_url, RequestType.GET, ResponseType.LEAGUE_IMAGES);
        task.execute();
    }

    /**
     * Method for /leagues?league_id=<league_id>
     * Request Type: GET
     *
     * @param callback
     * @param leagueID
     */
    public void getLeague(APICallback callback, int leagueID) {
        String api_url = String.format("https://fantasy-api.formula1.com/f1/2022/leaderboards/leagues?v=1&league_id=%d", leagueID);
        CallAPITask task = new CallAPITask(callback, api_url, RequestType.GET, ResponseType.GET_LEAGUE);
        task.execute();
    }

    /**
     * Overloaded for method
     *
     * @param callback
     * @param leagueCode
     */
    public void getLeague(APICallback callback, String leagueCode) {
        String api_url = String.format("https://fantasy-api.formula1.com/f1/2022/leagues?v=1&code=%s", leagueCode);
        CallAPITask task = new CallAPITask(callback, api_url, RequestType.GET, ResponseType.GET_LEAGUE);
        task.execute();
    }

    /**
     * Method for /league_entrants? for joining leagues
     * Request Type: POST
     *
     * @param callback
     * @param league
     */
    public void joinLeague(APICallback callback, League league) {
        String api_url = "https://fantasy-api.formula1.com/f1/2022/league_entrants?v=1";
        // convert the team to a json
        JSONObject payload = league.toJSON();
        // spin up the task
        CallAPITask task = new CallAPITask(callback, api_url, RequestType.POST, ResponseType.JOIN_LEAGUE, payload);
        task.execute();
    }

    /**
     * Method for /players
     * Request Type: GET
     *
     * @param callback
     */
    public void getPlayers(APICallback callback) {
        String api_url = "https://fantasy-api.formula1.com/f1/2022/players?v=1";
        // spin up the task
        CallAPITask task = new CallAPITask(callback, api_url, RequestType.GET, ResponseType.PLAYERS);
        task.execute();
    }

    /**
     * Gets a users picked teams
     * Request Type: GET
     *
     * @param callback
     * @param game_period - The current race for this upcoming weekend
     */
    public void getPickedTeams(APICallback callback, int game_period) {
        String api_url = String.format("https://fantasy-api.formula1.com/f1/2022/picked_teams?v=1&game_period_id=%d&my_current_picked_teams=true&my_next_picked_teams=false", game_period);
        //spin up the task
        CallAPITask task = new CallAPITask(callback, api_url, RequestType.GET, ResponseType.PICKED_TEAMS);
        task.execute();
    }

    /**
     * Creates a new team
     * Request Type: POST
     *
     * @param callback
     * @param team
     */
    public void createTeam(APICallback callback, Team team) {
        String api_url = "https://fantasy-api.formula1.com/f1/2022/picked_teams?v=1";
        // convert the team to a json
        JSONObject payload = team.toJSON();
        // spin up the task
        CallAPITask task = new CallAPITask(callback, api_url, RequestType.POST, ResponseType.PICKED_TEAMS, payload);
        task.execute();
    }

    /**
     * Updates a team
     * Request Type: PUT
     *
     * @param callback
     * @param team
     */
    public void updateTeam(APICallback callback, Team team) {
        String api_url = String.format("https://fantasy-api.formula1.com/f1/2022/picked_teams/%s?v=1", team.parentID);
        JSONObject payload = team.toJSON();
        CallAPITask task = new CallAPITask(callback, api_url, RequestType.PUT, ResponseType.PICKED_TEAMS, payload);
        task.execute();
    }

    /**
     * CallAPITask is the actual AsyncTask that implements the getting/posting/putting of the API Calls
     */
    private class CallAPITask extends AsyncTask<String, Integer, JSONObject> {
        private APICallback callback;
        private String api_url;
        private RequestType requestType;
        private ResponseType responseType;
        private JSONObject payload;
        private int statusCode;


        /**
         * Constructor for CallAPITask for requests that do not require a payload
         *
         * @param onFinish {APICallback} - the callback that will be called onPostExecute
         * @param api_url  {String} - the endpoint to be called
         * @param reqType  {RequestType} - the request method
         * @param respType {ResponseType} - the type of response that will be returned
         */
        public CallAPITask(APICallback onFinish, String api_url, RequestType reqType, ResponseType respType) {
            this.callback = onFinish;
            this.api_url = api_url;
            this.requestType = reqType;
            this.responseType = respType;
        }

        /**
         * Overloaded constructor for handling requests that have a payload such as POST and PUT
         *
         * @param onFinish {APICallback} - the callback that will be called onPostExecute
         * @param api_url  {String} - the endpoint to be called
         * @param reqType  {RequestType} - the request method
         * @param respType {ResponseType} - the type of response that will be returned
         * @param payload  {JSONObject} - the JSON payload of the request
         */
        public CallAPITask(APICallback onFinish, String api_url, RequestType reqType, ResponseType respType, JSONObject payload) {
            this.callback = onFinish;
            this.api_url = api_url;
            this.requestType = reqType;
            this.responseType = respType;
            this.payload = payload;
        }

        /**
         * doInBackground performs the actual prepping of the request as well as the requesting
         * and handling the responses, and  error handling
         *
         * @param strings
         * @return
         */
        @Override
        protected JSONObject doInBackground(String... strings) {
            String json = "";
            String line;

            // form the request
            try {
                URL url = new URL(this.api_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // form the headers
                for (String prop : headers.keySet()) {
                    String value = headers.get(prop);
                    conn.addRequestProperty(prop, value);
                }

                // set request type and preflight checks
                switch (this.requestType) {
                    case GET:
                        conn.setRequestMethod(RequestType.GET.toString());
                        conn.setInstanceFollowRedirects(true);
                        break;
                    case POST:
                        conn.setRequestMethod(RequestType.POST.toString());
                        conn.setInstanceFollowRedirects(false);
                        conn.setDoOutput(true);
                        // Write the payload to the request
                        try (OutputStream os = conn.getOutputStream()) {
                            byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }
                        break;
                    case PUT:
                        conn.setRequestMethod(RequestType.PUT.toString());
                        conn.setInstanceFollowRedirects(false);
                        conn.setDoOutput(true);
                        // Write the payload to the request
                        try (OutputStream os = conn.getOutputStream()) {
                            byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }
                        break;
                    default:
                        break;
                }

                System.out.println("--------- MAKING REQUEST -----------");
                System.out.println("URL: " + this.api_url);
                for (String prop : headers.keySet()) {
                    System.out.print(prop + " : ");
                    System.out.println(conn.getRequestProperty(prop));
                }

                if (requestType == RequestType.POST || requestType == RequestType.PUT) {
                    System.out.println("Body: " + payload.toString());
                }
                System.out.println("------------------------------------");

                conn.connect();
                // make the request
                InputStream _is;
                if (conn.getResponseCode() == 201) {
                    // so it turns out that sometimes they just dont feel like gzipping ????????
                    System.out.println("CREATED");
                    _is = conn.getInputStream();
                } else if (conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                    System.out.println("GOOD REQUEST");
                    _is = new GZIPInputStream(conn.getInputStream());
                } else {
                    System.out.println(conn.getResponseCode());
                    System.out.println("BAD REQUEST");
                    _is = conn.getErrorStream();
                }

                this.statusCode = conn.getResponseCode();

                BufferedReader in = new BufferedReader(new InputStreamReader(_is));
                while ((line = in.readLine()) != null) {
                    json += line;
                }

                in.close();

                conn.disconnect();
                return new JSONObject(json);
            } catch (Exception e) {
                System.out.println(e.getClass());
                e.printStackTrace();
                System.out.println("AUUUGGGGHHHH >:( problem making request");
                return null;
            }
        }

        /**
         * Invokes the callback provided from the caller
         *
         * @param resp
         */
        protected void onPostExecute(JSONObject resp) {
            // hand the JSON back to the caller
            this.callback.onFinish(resp, this.responseType, this.statusCode);
        }
    }
}
