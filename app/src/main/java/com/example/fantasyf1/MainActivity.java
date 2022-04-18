package com.example.fantasyf1;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        LoginFragment frag = new LoginFragment();
        frag.setContainerActivity(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.web_frag, frag)
                .addToBackStack(null)
                .commit();
    }

    public void onAfterLogin() {
        System.out.println("CALLED");
        CookieManager manager = CookieManager.getInstance();
        System.out.println(manager.getCookie("https://api.formula1.com/v2/account/subscriber/authenticate/by-password"));
    }


}