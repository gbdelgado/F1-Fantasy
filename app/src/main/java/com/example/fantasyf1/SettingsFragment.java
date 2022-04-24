package com.example.fantasyf1;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private Activity containerActivity = null;
    private SharedPreferences sharedPrefs = null;
    private SharedPreferences.Editor editor = null;

    public SettingsFragment() {}

    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
        sharedPrefs = containerActivity.getSharedPreferences("pref", Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    /**
     * @TODO -
     *
     * gets the currently stored theme preference from SharedPreferences, inverts the theme, and then
     * updates the SharedPreferences
     */
    public void toggleTheme() {
        // get theme preference
        int themeMode = sharedPrefs.getInt("THEME_MODE", -1);
        if (themeMode == AppCompatDelegate.MODE_NIGHT_NO) {
            themeMode = AppCompatDelegate.MODE_NIGHT_YES;
        } else {
            themeMode = AppCompatDelegate.MODE_NIGHT_NO;
        }

        // put new them mode in preference
        editor.putInt("THEME_MODE", themeMode);
        editor.apply();

        // set the theme
        AppCompatDelegate.setDefaultNightMode(themeMode);

        ((HomepageActivity) containerActivity).getSupportFragmentManager().popBackStack();
    }

}
