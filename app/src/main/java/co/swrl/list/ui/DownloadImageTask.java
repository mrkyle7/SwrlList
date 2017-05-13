package co.swrl.list.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.InputStream;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView bmImage;
    private ProgressBar bmSpinner;

    public DownloadImageTask(ImageView bmImage, ProgressBar bmSpinner) {
        this.bmImage = bmImage;
        this.bmSpinner = bmSpinner;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
        bmSpinner.setVisibility(INVISIBLE);
        bmImage.setVisibility(VISIBLE);
    }
}
