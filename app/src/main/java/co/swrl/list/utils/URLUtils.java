package co.swrl.list.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class URLUtils {
    public static void openURL(Uri uri, Activity activity){
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }
}
