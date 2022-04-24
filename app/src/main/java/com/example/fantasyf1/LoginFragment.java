package com.example.fantasyf1;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookiePolicy;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    WebView webView;
    MainActivity containerActivity = null;
    boolean needsRetry = false;

    public LoginFragment() {
        // Required empty public constructor
    }

    public void setContainerActivity(MainActivity containerActivity) {
        this.containerActivity = containerActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        CookieManager.getInstance().setAcceptCookie(true);
        webView = v.findViewById(R.id.web_view);
        webView.addJavascriptInterface(new WebAppInterface(getContext()), "Android");

        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setWebViewClient(new LoginClient());
        webView.loadUrl("https://account.formula1.com/#/en/register?lead_source=web_fantasy&redirect=https%3A%2F%2Ffantasy.formula1.com%2Fapp%2F%23%2Fhttps%3A%2F%2Ffantasy.formula1.com%2F");

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.containerActivity.onAfterLogin(this.needsRetry);
    }

    /**
     * @NOTE @NOTE @NOTE @NOTE @NOTE @NOTE @NOTE @NOTE @NOTE
     * NEED TO LOOK OUT FOR THIS REQUEST AND THROW AN ERROR
     * https://account.formula1.com/#/en/logout?
     * <p>
     * idk why but sometimes itll just log me out halfway through logging
     * maybe its bot control, but if this happens we should dump the cookies
     * and try again
     */
    private class LoginClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // This is a pretty good indication that we have been let in to the website
            System.out.println("LOADING: " + url);
            // sometimes this logs us out becuz cookie old so retry
            if (url.startsWith("https://account.formula1.com/#/en/logout?")) {
                System.out.println("Failed Login");
                needsRetry = true;
                containerActivity.getSupportFragmentManager().popBackStack();
            }
            return false;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if(request.getUrl().toString().startsWith("https://fantasy-api.formula1.com/f1/2022/live_stats?v=1&game_period_id=4")) {
                System.out.println("STOPPING: " + request.getUrl().toString());
                containerActivity.getSupportFragmentManager().popBackStack();
                return null;
            }

            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            System.out.println("FINISHED: " + url);
            super.onPageFinished(view, url);
        }
    }

    private class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            this.mContext = c;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }
}