package com.example.fantasyf1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerListActivity extends AppCompatActivity {

    Team team;
    PlayerAdapter playerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        team = (Team) getIntent().getSerializableExtra("TEAM");
        TextView textView = findViewById(R.id.header_team_name);
        textView.setText(team.name);

        setPlayerList();
    }

    public void onClickHandler(View view) {
        switch (view.getId()) {
            case R.id.image_share:
                shareTeam();
                break;
            default:
                System.out.println("");
        }
    }

    /**
     * create ListView for team's players
     */
    private void setPlayerList() {
        ListView listView = findViewById(R.id.listview_players_teampage);
        playerAdapter = new PlayerAdapter(this, R.layout.row_player, team.players);
        listView.setAdapter(playerAdapter);
    }

    private void shareTeam() {
        ListView listView = findViewById(R.id.listview_players_teampage);

        // take screenshot of team
        Bitmap bitmap = Bitmap.createBitmap(listView.getWidth(), listView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        listView.draw(canvas);

        // write screenshot to file
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "screenshot.jpg");
        OutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOut);
        try {
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // get the file
        file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "screenshot.jpg");
        Uri photoURI = FileProvider.getUriForFile(this, "com.example.fantasyf1.android.provider", file);

        // share it!
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, photoURI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        startActivity(intent);
    }
}