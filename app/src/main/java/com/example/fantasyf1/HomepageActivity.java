package com.example.fantasyf1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class HomepageActivity extends AppCompatActivity {

    SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
    }

    /**
     * on click handler function for any on click event in HomepageActivity. performs appropriate
     * actions depending on what onClick fired
     * @param view
     */
    public void onClickHandler(View view) {
        switch (view.getId()) {
            case R.id.image_settings:
                 settingsFragment = new SettingsFragment();
                 settingsFragment.setContainerActivity(this);
                 getSupportFragmentManager().beginTransaction()
                         .replace(R.id.layout_home_page, settingsFragment)
                         .addToBackStack(null)
                         .commit();
        }
    }

    /**
     * @TODO - BUG: toggling twice in a row causes loss of reference to settingsFragment
     *
     * onClick handler for settingsFragment
     * @param view
     */
    public void settingsFragmentHandler(View view) {
        settingsFragment.toggleTheme();

        getSupportFragmentManager()
                .beginTransaction()
                .remove(settingsFragment)
                .commit();
    }

}