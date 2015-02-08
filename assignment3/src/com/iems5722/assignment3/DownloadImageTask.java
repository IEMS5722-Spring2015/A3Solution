package com.iems5722.assignment3;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	private static final String TAG = "DownloadImageTask";
    ImageView imgView;

    public DownloadImageTask(ImageView imgView) {
        this.imgView = imgView;
    }

    protected Bitmap doInBackground(String... param) {
        String imgUrl = param[0];
        Bitmap bmp = null;
        try {
        	Log.d(TAG, "Downloading image from " + imgUrl);
            InputStream in = new java.net.URL(imgUrl).openStream();
            bmp = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return bmp;
    }

    protected void onPostExecute(Bitmap result) {
    	imgView.setImageBitmap(result);
    }
}
