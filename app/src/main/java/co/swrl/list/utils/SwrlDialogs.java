package co.swrl.list.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import co.swrl.list.R;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.actions.SwrlCoActions;

import static android.content.DialogInterface.BUTTON_NEUTRAL;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static co.swrl.list.utils.URLUtils.openURL;

public class SwrlDialogs {
    private final Activity activity;

    public SwrlDialogs(Activity activity) {
        this.activity = activity;
    }

    public static void showReviewPopUp(final Swrl swrlToAction, final SwrlPreferences preferences, final Activity activity) {
        if (preferences.loggedIn() && swrlToAction.getId() != 0) {
            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            final View dialogLayout = inflater.inflate(R.layout.review_popup, null);
            builder.setView(dialogLayout);
            boolean userIsAuthor = swrlToAction.getAuthorId() == preferences.getUserID();
            if (userIsAuthor || swrlToAction.getAuthor().isEmpty()){
                builder.setTitle("So, what did you think?");
            } else {
                builder.setTitle("Let " + swrlToAction.getAuthor() + " know what you thought!");
            }
            builder.setNegativeButton("Respond Later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton("Send Response", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final EditText comments = (EditText) dialogLayout.findViewById(R.id.comment_input);
                    final String comment = String.valueOf(comments.getText());
                    RadioGroup responseOptions = (RadioGroup) dialogLayout.findViewById(R.id.response_radio);
                    final String response;
                    switch(responseOptions.getCheckedRadioButtonId()){
                        case R.id.radio_loved_it:
                            response = SwrlCoActions.LOVED_IT;
                            break;
                        case R.id.radio_not_bad:
                            response = SwrlCoActions.NOT_BAD;
                            break;
                        case R.id.radio_not_for_me:
                            response = SwrlCoActions.NOT_FOR_ME;
                            break;
                        default:
                            response = "";
                            break;
                    }
                    Log.d("REVIEW_POPUP", "comments: " + comment);
                    Log.d("REVIEW_POPUP", "Response: " + response);
                    new AsyncTask<Void, Void, Void>(){

                        @Override
                        protected Void doInBackground(Void... voids) {
                            if (!response.isEmpty()){
                                SwrlCoActions.respond(swrlToAction, response, preferences, null);
                            }
                            if (!comment.isEmpty()){
                                SwrlCoActions.comment(swrlToAction, comment, preferences, null);
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            if (!response.isEmpty() || !comment.isEmpty()){
                                Snackbar.make(activity.findViewById(android.R.id.content), "Response sent!", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }.execute();
                }
            });
            builder.show();
        }
    }

    public void buildAndShowWhatsNewDialog() {
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
                        openURL(Uri.parse("https://github.com/mrkyle7/SwrlList/blob/master/app/src/main/play/en-GB/whatsnew"), activity);
                    }
                });
    }

    private void setButtonIDs(AlertDialog whatsNewDialog) {
        Button dismissWhatsNew = whatsNewDialog.getButton(BUTTON_POSITIVE);
        dismissWhatsNew.setId(R.id.dismissWhatsNewButton);
        Button whatsMore = whatsNewDialog.getButton(BUTTON_NEUTRAL);
        whatsMore.setId(R.id.whatsNewMoreButton);
    }
}
