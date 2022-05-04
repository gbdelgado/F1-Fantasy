/**
 * @file:           WebViewFragment.java
 * @author:         CJ Larsen
 * @description:    this fragment opens a WebView of a provided URL. special settings need to be set
 *                  to allow the url to load properly
 */

package com.example.fantasyf1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

public class WebViewFragment extends Fragment {

    public WebViewFragment() {}

    /**
     * takes the url provided and starts a WebView with it
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return inflated Webbed Viewed
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_webview, container, false);

        // pass url for WebView
        String url = this.getArguments().getString("URL");
        WebView webView = v.findViewById(R.id.web_view);

        // stuff from the internet
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl(url);
        return v;
    }

}
