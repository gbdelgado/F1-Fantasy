package com.example.fantasyf1;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONObject;

import java.net.CookieHandler;


public class MainActivity extends AppCompatActivity implements APICallback {

    @Override
    public void onFinish(JSONObject response) {
        System.out.println("JSON OUT");
        System.out.println(response.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        setContentView(R.layout.activity_main);
        LoginFragment frag = new LoginFragment();
        frag.setContainerActivity(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.web_frag, frag)
                .addToBackStack(null)
                .commit();
    }

    public void onAfterLogin() {
        System.out.println("LOGGED IN");
        FantasyManager manager = new FantasyManager();
//        manager.getPlayers(this::onFinish);
//        manager.getBoosters(this::onFinish);
//        manager.getSeason(this::onFinish);
//        manager.getTeams(this::onFinish);
        manager.getPickedTeams(this::onFinish, 4);
    }



}