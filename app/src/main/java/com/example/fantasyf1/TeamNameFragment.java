/**
 * TeamNameFrament.java
 * This fragment is a popup for when a user first creates a team. This interface allows them to
 * set the team name, It is also responsible for verifying that the use has met the requirements for
 * a valid teamname
 *
 */
package com.example.fantasyf1;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TeamNameFragment extends Fragment {
    private String teamName;
    private Activity containerActivity;

    public TeamNameFragment() {
        // Required empty public constructor
    }

    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }

    /**
     * Tells the parent activity that our team name has been set
     */
    @Override
    public void onDetach() {
        super.onDetach();
        if(teamName != null) {
            // tell the activity we got a name
            ((CreateTeamActivity) getActivity()).setTeamName(teamName);
        }
    }

    /**
     * onCreateView sets the stage for the activity as well as sets the event listener for linting
     * the name the user types in to make sure it meets the API requirements
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_team_name, container, false);
        // set an event listener for the button
        // NAME MUST BE 3 < name.length < 50
        Button confirm = v.findViewById(R.id.button_confirm_name);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the text from edit text
                EditText editText = v.findViewById(R.id.edit_text_team_name);
                teamName = editText.getText().toString();

                // verify that it fits the name requirements
                if(teamName.length() < 3) {
                    Toast.makeText(getContext(), "Team Name must be at least 3 characters", Toast.LENGTH_SHORT).show();
                    return;
                } else if(teamName.length() > 50) {
                    Toast.makeText(getContext(), "Team Name must be less than 50 characters", Toast.LENGTH_LONG).show();
                    return;
                }

                // if we are here we had a good name dismount
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return v;
    }
}