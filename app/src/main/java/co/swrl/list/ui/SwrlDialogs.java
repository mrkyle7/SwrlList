package co.swrl.list.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import co.swrl.list.R;

import static android.content.DialogInterface.BUTTON_NEUTRAL;
import static android.content.DialogInterface.BUTTON_POSITIVE;

class SwrlDialogs {
    private final Activity activity;

    SwrlDialogs(Activity activity) {
        this.activity = activity;
    }

    void buildAndShowWhatsNewDialog() {
        AlertDialog whatsNewDialog = buildWhatsNewDialog();
        whatsNewDialog.show();
        setButtonIDs(whatsNewDialog);
        TextView whatsNewMessage = (TextView) whatsNewDialog.findViewById(R.id.whatsNewMessage);

        String whatsNewLatest = activity.getResources().getString(R.string.whatsNewLatest);
        whatsNewMessage.setText(Html.fromHtml(whatsNewLatest));
    }

    private AlertDialog buildWhatsNewDialog() {
        View whatsNew = getWhatsNewView();
        return getWhatsNewDialogBuilder(whatsNew).create();
    }

    @SuppressLint("InflateParams")
    private View getWhatsNewView() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        return inflater.inflate(R.layout.whatsnew, null);
    }

    private Builder getWhatsNewDialogBuilder(View view) {
        return new Builder(activity)
                .setView(view)
                .setTitle(R.string.whatsNewTitle)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton(R.string.whatsNewMoreButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openURL(Uri.parse("https://github.com/mrkyle7/SwrlList/blob/master/app/src/main/play/en-GB/whatsnew"));
                    }
                });
    }

    private void setButtonIDs(AlertDialog whatsNewDialog) {
        Button dismissWhatsNew = whatsNewDialog.getButton(BUTTON_POSITIVE);
        dismissWhatsNew.setId(R.id.dismissWhatsNewButton);
        Button whatsMore = whatsNewDialog.getButton(BUTTON_NEUTRAL);
        whatsMore.setId(R.id.whatsNewMoreButton);
    }

    private void openURL(Uri uri){
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }
}
