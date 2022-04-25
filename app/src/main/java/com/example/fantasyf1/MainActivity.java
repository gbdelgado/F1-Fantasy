package com.example.fantasyf1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONObject;

import java.net.CookieHandler;


public class MainActivity extends AppCompatActivity implements APICallback {

    @Override
    public void onFinish(JSONObject response, FantasyManager.ResponseType respType) {
        System.out.println("JSON OUT");
        System.out.println(response.toString());
    }

    LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get and set theme preference
        SharedPreferences sharedPrefs = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        int themeMode = sharedPrefs.getInt("THEME_MODE", AppCompatDelegate.MODE_NIGHT_UNSPECIFIED);
        AppCompatDelegate.setDefaultNightMode(themeMode);

        setContentView(R.layout.activity_main);
    }

    /**
     * start login fragment for user auth
     * @param view
     */
    public void startLogin(View view) {
        if(view == null) {
            System.out.println("Restarting");
        }
        // clear any old cookies
//        CookieManager.getInstance().removeAllCookies(null);
//        CookieManager.getInstance().flush();

        loginFragment = new LoginFragment();
        loginFragment.setContainerActivity(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.web_frag, loginFragment)
                .addToBackStack(null)
                .commit();
    }

    public void onAfterLogin(boolean needsRetry) {
        if(needsRetry) {
            System.out.println("Needs Retry");
            startLogin(null);
            return;
        }
        System.out.println("LOGGED IN");
        FantasyManager manager = new FantasyManager();
//        manager.getPlayers(this::onFinish);
//        manager.getBoosters(this::onFinish);
//        manager.getSeason(this::onFinish);
//        manager.getTeams(this::onFinish);
        manager.getPickedTeams(this::onFinish, 4);

        Intent myIntent = new Intent(this, HomepageActivity.class);
        this.startActivity(myIntent);
    }

}