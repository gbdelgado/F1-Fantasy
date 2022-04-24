package com.example.fantasyf1;

import android.os.AsyncTask;
import android.webkit.CookieManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    };

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
        for(String url : test) {
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
        headers.put("x-build", "4c1e4a96");
        headers.put("x-project", "flutter");
        headers.put("x-version", "2.5.0");
    }

    /**
     * @param callback
     * @NOTE DOESNT WORK IDK WHY : (
     * Method for /users
     * GET Request
     * @deprecated doesnt work
     */
    public void getUser(APICallback callback) {
        String api_url = "https://fantasy-api.formula1.com/f1/2022/users?v=1&current=true";
        // spin up the task
        CallAPITask task = new CallAPITask(callback, api_url, RequestType.GET);
        task.execute();
    }

    /**
     * @param callback
     * @NOTE DOESNT WORK DOESNT WORK DONT USE ASKLDJ:LDGFKJHSD:LKFJA:SFJSF:LKAJDSF:LASKDFJ
     * Method for /2022 route
     * GET Request
     * @deprecated
     */
    public void getSeason(APICallback callback) {
        String api_url = "https://fantasy-api.formula1.com/f1/2022?v=1";
        // spin up the task
        CallAPITask task = new CallAPITask(callback, api_url, RequestType.GET);
        task.execute();
    }

    /**
     * Method for /teams
     *
     * @param callback
     */
    public void getTeams(APICallback callback) {
        String api_url = "https://fantasy-api.formula1.com/f1/2022/teams?v=1";
        // spin up the task
        CallAPITask task = new CallAPITask(callback, api_url, RequestType.GET);
        task.execute();
    }

    /**
     * Method for /boosters
     *
     * @param callback
     */
    public void getBoosters(APICallback callback) {
        String api_url = "https://fantasy-api.formula1.com/f1/2022/boosters?v=1";
        // spin up the task
        CallAPITask task = new CallAPITask(callback, api_url, RequestType.GET);
        task.execute();
    }

    /**
     * Method for /players
     *
     * @param callback
     */
    public void getPlayers(APICallback callback) {
        String api_url = "https://fantasy-api.formula1.com/f1/2022/players?v=1";
        // spin up the task
        CallAPITask task = new CallAPITask(callback, api_url, RequestType.GET);
        task.execute();
    }

    /**
     * @param callback
     * @param game_period - The current race for this upcoming weekend
     */
    public void getPickedTeams(APICallback callback, int game_period) {
        String api_url = String.format("https://fantasy-api.formula1.com/f1/2022/picked_teams?v=1&game_period_id=%d&my_current_picked_teams=true&my_next_picked_teams=false", game_period);
        //spin up the task
        CallAPITask task = new CallAPITask(callback, api_url, RequestType.GET);
        task.execute();
    }

    /**
     *
     */
    private class CallAPITask extends AsyncTask<String, Integer, JSONObject> {
        private APICallback callback;
        private String api_url;
        private RequestType requestType;

        public CallAPITask(APICallback onFinish, String api_url, RequestType type) {
            this.callback = onFinish;
            this.api_url = api_url;
            this.requestType = type;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            String json = "";
            String line;

            // form the request
            try {
                URL url = new URL(this.api_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setInstanceFollowRedirects(true);
                // set request type
                switch (this.requestType) {
                    case GET:
                        conn.setRequestMethod(RequestType.GET.toString());
                        break;
                    case POST:
                        conn.setRequestMethod(RequestType.POST.toString());
                        break;
                    case PUT:
                        conn.setRequestMethod(RequestType.PUT.toString());
                        break;
                    default:
                        break;
                }

                // form the headers
                for (String prop : headers.keySet()) {
                    String value = headers.get(prop);
                    conn.addRequestProperty(prop, value);
                }

                System.out.println("--------- MAKING REQUEST -----------");
                System.out.println("URL: " + this.api_url);
                for (String prop : headers.keySet()) {
                    System.out.print(prop + " : ");
                    System.out.println(conn.getRequestProperty(prop));
                }
                System.out.println("------------------------------------");

                conn.connect();
                // make the request
                InputStream _is;
                if (conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                    System.out.println("GOOD REQUEST");
                    _is = new GZIPInputStream(conn.getInputStream());
                } else {
                    System.out.println(conn.getResponseCode());
                    System.out.println("BAD REQUEST");
                    _is = conn.getErrorStream();
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(_is));
                while ((line = in.readLine()) != null) {
                    json += line;
                }

                in.close();
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
            this.callback.onFinish(resp);
        }
    }
}
