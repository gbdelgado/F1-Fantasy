package com.example.fantasyf1;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HelpFragment extends Fragment {

    private String rulesURL = "https://fantasy.formula1.com/game-rules";
    private String pointsURL = "https://fantasy.formula1.com/points-scoring";
    private String faqURL = "https://fantasy.formula1.com/faq";

    private Activity containerActivity = null;

    public HelpFragment() {}

    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    public void onClickHandler(View view) {
        Bundle args = new Bundle();

        switch (view.getId()) {
            case R.id.button_how_to_play:
                startHelpPageFragment();
                break;
            case R.id.button_rules:
                args.putString("URL", rulesURL);
                startWebViewFragment(args);
                break;
            case R.id.button_points_scoring:
                args.putString("URL", pointsURL);
                startWebViewFragment(args);
                break;
            case R.id.button_faq:
                args.putString("URL", faqURL);
                startWebViewFragment(args);
                break;
        }

    }

    private void startWebViewFragment(Bundle args) {
        WebViewFragment webViewFragment = new WebViewFragment();
        webViewFragment.setArguments(args);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                .replace(R.id.layout_help_page, webViewFragment)
                .addToBackStack(null)
                .commit();
    }

    private void startHelpPageFragment() {
        HelpPageFragment helpPageFragment = new HelpPageFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                .replace(R.id.layout_help_page, helpPageFragment)
                .addToBackStack(null)
                .commit();
    }
}