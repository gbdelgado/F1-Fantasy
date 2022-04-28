package com.example.fantasyf1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.loader.content.AsyncTaskLoader;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class PlayerAdapter extends ArrayAdapter<Player> {

    private Context context;
    private ArrayList<Player> players;

    public PlayerAdapter(Context context, int rowLayoutID, ArrayList<Player> players) {
        super(context, rowLayoutID, players);
        this.context = context;
        this.players = players;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.row_player, null);
        }

        Player player = players.get(position);

        // ImageView Here
        DownloadImageTask downloadImageTask = new DownloadImageTask(view.findViewById(R.id.row_player_image), player.imageURL);
        downloadImageTask.execute();

        TextView name = (TextView) view.findViewById(R.id.row_player_name);
        String nameText = player.turbo ? player.displayName + "\uD83D\uDD25" : player.displayName;
        name.setText(nameText);

        TextView points = (TextView) view.findViewById(R.id.row_player_points);
        String pointsText = "Points: " + player.seasonScore;
        points.setText(pointsText);

        TextView value = (TextView) view.findViewById(R.id.row_player_value);
        String valueText = "Value: " + player.price + "M";
        value.setText(valueText);

        return view;
    }

}
