package com.example.fantasyf1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MainActivity extends AppCompatActivity {

    LoginFragment loginFragment;
    SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * start login fragment for user auth
     * @param view
     */
    public void startLogin(View view) {
        loginFragment = new LoginFragment();
        loginFragment.setContainerActivity(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.web_frag, loginFragment)
                .addToBackStack(null)
                .commit();
    }

    public void onAfterLogin() {
        System.out.println("CALLED");
        CookieManager manager = CookieManager.getInstance();
        System.out.println(manager.getCookie("https://api.formula1.com/v2/account/subscriber/authenticate/by-password"));
    }

    /**
     * onClick handler for settingsFragment
     * @param view
     */
    public void settingsFragmentHandler(View view) {
        settingsFragment.toggleTheme();
    }

}