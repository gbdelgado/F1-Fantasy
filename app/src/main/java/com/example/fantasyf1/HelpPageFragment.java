/**
 * @file:           HelpPageFragment.java
 * @author:         CJ Larsen
 * @description:    basic fragment displaying info for how to play the game
 */

package com.example.fantasyf1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HelpPageFragment extends Fragment {

    public HelpPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help_page, container, false);
    }
}