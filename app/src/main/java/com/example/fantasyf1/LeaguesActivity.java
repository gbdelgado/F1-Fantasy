package com.example.fantasyf1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class LeaguesActivity extends AppCompatActivity implements APICallback {

    @Override
    public void onFinish(JSONObject response, FantasyManager.ResponseType respType, int statusCode) {
        System.out.println("JSON OUT");
        System.out.println(response.toString());

        jsonResponses.put(respType.toString().toLowerCase(Locale.ROOT), response);

        switch (respType) {
            case LEAGUE_ENTRANTS:
                parseLeagues();
                setLeaguesList();
                break;
            case GET_LEAGUE:
                System.out.println(!joinCode.equals(""));
                if (!joinCode.equals("")) {
                    System.out.println("XXX: JOINING?");
                    joinLeague();
                } else {
                    setLeaderboard();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("LEAGUE", leagues.get(calledLeagueIndex));

                    leaderboardFragment = new LeaderboardFragment();
                    leaderboardFragment.setArguments(bundle);
                    leaderboardFragment.setContainerActivity(this);
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.layout_leagues_page, leaderboardFragment)
                            .addToBackStack(null)
                            .commit();
                }
                break;
            case JOIN_LEAGUE:
                System.out.println("XXX: LEAGUE JOINED - RESTARTING");

                Intent intent = new Intent(this, LeaguesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("TEAMS", teams);
                startActivity(intent);
                break;
        }
    }

    FantasyManager manager;
    LeaderboardFragment leaderboardFragment;
    JoinLeagueFragment joinLeagueFragment;
    PickLeagueTeamFragment pickLeagueTeamFragment;

    HashMap<String, JSONObject> jsonResponses = new HashMap<>();
    HashMap<Integer, Team> teams;
    ArrayList<League> leagues = new ArrayList<>();
    LeagueAdapter leagueAdapter;

    int calledLeagueIndex;
    int selectedTeamSlot = 1;
    View pastSelection = null;
    String joinCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leagues);

        teams = (HashMap<Integer, Team>) getIntent().getSerializableExtra("TEAMS");

        manager = new FantasyManager();
        manager.getLeagueEntrants(this::onFinish);
    }

    public void onClickHandler(View view) {
        switch (view.getId()) {
            case R.id.row_league:
                ListView listView = (ListView) view.getParent();

                // get League obj for selected league
                League tempLeague = leagues.get(listView.getPositionForView(view));
                calledLeagueIndex = listView.getPositionForView(view);

                manager.getLeague(this::onFinish, tempLeague.id);
                break;
            case R.id.image_alt_share:
                checkContactsPermission();
                break;
            case R.id.button_join_league:
                joinLeagueFragment = new JoinLeagueFragment();
                joinLeagueFragment.setContainerActivity(this);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
                        .add(R.id.layout_leagues_page, joinLeagueFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.button_join_league_code:

                joinCode = ((EditText) findViewById(R.id.edit_text_league_join_code)).getText().toString();

//                getSupportFragmentManager().beginTransaction()
//                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
//                        .remove(joinLeagueFragment)
//                        .commit();

                pickLeagueTeamFragment = new PickLeagueTeamFragment();
                pickLeagueTeamFragment.setContainerActivity(this);

                Bundle bundle = new Bundle();
                bundle.putSerializable("TEAMS", teams);
                pickLeagueTeamFragment.setArguments(bundle);

                System.out.println("XXX: " + joinCode);

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .add(R.id.layout_leagues_page, pickLeagueTeamFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.row_team:
                ListView anotherListView = (ListView) view.getParent();
                // slots are 1-indexed
                int slot = anotherListView.getPositionForView(view) + 1;
                selectedTeamSlot = slot;

                System.out.println("XXX: SLOT- " + slot);

                // recent the color for previously selected team
                if (pastSelection != null) {
                    int color = Color.TRANSPARENT;
                    Drawable background = view.getBackground();
                    if (background instanceof ColorDrawable) {
                        color = ((ColorDrawable) background).getColor();
                    }
                    pastSelection.setBackgroundColor(color);
                }
                pastSelection = view;

                view.setBackgroundColor(getResources().getColor(R.color.faint_red));
                break;
            case R.id.button_confirm_team:
                manager.getLeague(this::onFinish, joinCode);
//
//                // create League obj
//
//                // set global team id for team leauge choice
//                League joinLeague = new League();
//                joinLeague.picked_team_id = teams.get(selectedTeamSlot).globalID;
//
//                manager.joinLeague(this::onFinish, joinLeague);

                // relaunch once successful join
//                Intent intent = new Intent(this, LeaguesActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.putExtra("TEAMS", teams);
//                startActivity(intent);
                break;
        }
    }

    private void parseLeagues() {
        try {
            JSONArray leaguesArr = jsonResponses.get("league_entrants")
                    .getJSONArray("league_entrants");

            for (int i = 0; i < leaguesArr.length(); i++) {
                JSONObject jsonLeague = (JSONObject) leaguesArr.get(i);
                League league = new League(jsonLeague);
                leagues.add(league);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setLeaguesList() {
        ListView listView = findViewById(R.id.listview_leagues_homepage);
        leagueAdapter = new LeagueAdapter(this, R.layout.row_league, leagues);
        listView.setAdapter(leagueAdapter);
    }

    private void setLeaderboard() {
        League league = leagues.get(calledLeagueIndex);

        JSONArray leaderboard = null;
        try {
            leaderboard = jsonResponses.get("get_league")
                    .getJSONObject("leaderboard")
                    .getJSONArray("leaderboard_entrants");
        } catch (Exception e) { e.printStackTrace(); }

        league.buildEntrantList(leaderboard);
    }

    private void checkContactsPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            startContactsFragment();
        } else {
            requestPermissions(new String[] { Manifest.permission.READ_CONTACTS }, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startContactsFragment();
            }
        }
    }

    private void startContactsFragment() {
        ContactFragment contactsFragment = new ContactFragment();
        contactsFragment.setContainerActivity(this);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.layout_leagues_page, contactsFragment)
                .addToBackStack(null)
                .commit();
    }

    public void onContactClick(View view) {
        String text = ((TextView) view).getText().toString();
        String id = text.substring(text.indexOf(" :: ") + 4);
        String contactInfo = "";

        // get phone number from contact
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
        while (phones.moveToNext()) {
            @SuppressLint("Range") String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactInfo = phoneNumber;
        }
        phones.close();

        // share it!
        Uri uri = Uri.parse("smsto:" + contactInfo);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", "Hey! Come join my F1 Fantasy League! Use code: " +
                leagues.get(calledLeagueIndex).code);
        startActivity(intent);
    }

    private void joinLeague() {
        League joinLeague = null;
        try {
            JSONArray leaguesArr = jsonResponses.get("league_entrants")
                    .getJSONArray("league_entrants");

            for (int i = 0; i < leaguesArr.length(); i++) {
                JSONObject jsonLeague = (JSONObject) leaguesArr.get(i);
                joinLeague = new League(jsonLeague);
            }
        } catch (Exception e) { e.printStackTrace(); }

        System.out.println("XXX: CREATED LEAGUE OBJ");

        // set global team id for team leauge choice
        joinLeague.picked_team_id = teams.get(selectedTeamSlot).globalID;
        System.out.println("XXX: LEAGUE GLOBAL ID - " + joinLeague.picked_team_id);

        manager.joinLeague(this::onFinish, joinLeague);
    }

}