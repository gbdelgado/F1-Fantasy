package com.example.fantasyf1;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Integer, Drawable> {

    private ImageView imageView;
    private String url;

    public DownloadImageTask(ImageView imageView, String url) {
        this.imageView = imageView;
        this.url = url;
    }

    protected Drawable doInBackground(String... strs) {
        Drawable image = null;
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            image = Drawable.createFromStream(is, "src name");
        } catch (Exception e) { e.printStackTrace(); }
        return image;
    }

    protected void onPostExecute(Drawable image) {
        imageView.setImageDrawable(image);
    }
}
