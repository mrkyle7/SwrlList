package co.swrl.swrllist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

class WhatsNewDialog extends AlertDialog {
    WhatsNewDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whatsnew);
        setTitle(R.string.whatsNewTitle);
        setButton(BUTTON_POSITIVE, String.valueOf(R.string.ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        setButton(BUTTON_NEUTRAL, String.valueOf(R.string.whatsNewMoreButtonText), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openURL(Uri.parse("https://github.com/mrkyle7/SwrlList/blob/master/app/src/main/play/en-GB/whatsnew"));
            }
        });
        setButtonIDs();
    }

    private void openURL(Uri uri){
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        super.getContext().startActivity(intent);
    }
    private void setButtonIDs() {
        Button dismissWhatsNew = this.getButton(BUTTON_POSITIVE);
        dismissWhatsNew.setId(R.id.dismissWhatsNewButton);
        dismissWhatsNew.setVisibility(View.VISIBLE);
        Button whatsMore = this.getButton(BUTTON_NEUTRAL);
        whatsMore.setId(R.id.whatsNewMoreButton);
        whatsMore.setVisibility(View.VISIBLE);
    }
}
