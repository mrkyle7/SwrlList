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
    private final ImageView bmImage;
    private final ProgressBar bmSpinner;
    private final ImageView bmIcon;
    private final int bmIconResource;

    public DownloadImageTask(ImageView bmImage, ProgressBar bmSpinner, ImageView bmIcon, int bmIconResource) {
        this.bmImage = bmImage;
        this.bmSpinner = bmSpinner;
        this.bmIcon = bmIcon;
        this.bmIconResource = bmIconResource;
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
        bmSpinner.setVisibility(INVISIBLE);
        if (result == null){
            bmIcon.setImageResource(bmIconResource);
            bmIcon.setVisibility(VISIBLE);
        } else {
            bmImage.setImageBitmap(result);
            bmImage.setVisibility(VISIBLE);
        }
    }
}
