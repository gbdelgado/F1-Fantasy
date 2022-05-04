/**
 * @file:           DownloadImageTask.java
 * @author:         CJ Larsen
 * @description:    AsyncTask class to allow loading images on background threads. takes in the url
 *                  as well as the ImageView-to-be-changed and will change it once downloaded
 */

package com.example.fantasyf1;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Integer, Drawable> {

    private ImageView imageView;
    private String url;

    /**
     * takes in the ImageView to be updated, as well as the URL to get the image from
     * @param imageView
     * @param url
     */
    public DownloadImageTask(ImageView imageView, String url) {
        this.imageView = imageView;
        this.url = url;
    }

    /**
     * downloads the image from the URL, converts it to a drawable, and returns it
     * @param strs
     * @return image Drawable
     */
    protected Drawable doInBackground(String... strs) {
        Drawable image = null;
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            image = Drawable.createFromStream(is, "src name");
        } catch (Exception e) { e.printStackTrace(); }
        return image;
    }

    /**
     * once ready, update the ImageView with the image
     * @param image
     */
    protected void onPostExecute(Drawable image) {
        imageView.setImageDrawable(image);
    }
}
