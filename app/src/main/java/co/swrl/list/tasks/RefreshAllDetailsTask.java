package co.swrl.list.tasks;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.swrl.list.collection.CollectionManager;
import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.search.Search;
import co.swrl.list.ui.activity.ListActivity;
import co.swrl.list.users.SwrlUserHelpers;

public class RefreshAllDetailsTask extends AsyncTask<Void, String, Void> {
    private static final String LOG_TAG = "RefreshAllDetailsTask";
    private ListActivity listActivity;
    private String updateMessage = "Refreshing all Details." +
            "\nThis may take a few minutes!";
    private final ProgressDialog dialog;
    private final AsyncTask mTask;

    public RefreshAllDetailsTask(ListActivity listActivity, final Context context) {
        this.listActivity = listActivity;
        dialog = new ProgressDialog(context);
        mTask = this;
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage(updateMessage);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mTask.cancel(true);
                dialog.dismiss();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Run in Background", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(LOG_TAG, "Refreshing all details");
        CollectionManager collectionManager = new SQLiteCollectionManager(listActivity.getApplicationContext());
        ArrayList<?> swrls = (ArrayList<?>) collectionManager.getAll();
        @SuppressLint("UseSparseArrays")
        Map<Integer, String> avatarCache = new HashMap<>();
        for (Object swrl : swrls) {
            Swrl mSwrl = (Swrl) swrl;
            publishProgress(String.valueOf(swrls.indexOf(swrl) + 1)
                    + " of "
                    + String.valueOf(swrls.size())
                    + "...\n"
                    + "Updating "
                    + mSwrl.getTitle());
            setEmptyAuthorIdToSelfIfLoggedIn(collectionManager, mSwrl);
            updateAvatarURL(collectionManager, avatarCache, mSwrl);
            updateSwrlDetails(collectionManager, mSwrl);
        }
        return null;
    }

    private void updateSwrlDetails(CollectionManager collectionManager, Swrl mSwrl) {
        if (mSwrl.getDetails() != null && mSwrl.getDetails().getId() != null && !mSwrl.getDetails().getId().isEmpty()) {
            Search search = mSwrl.getType().getSearch();
            Details details = search.byID(mSwrl.getDetails().getId());
            if (details != null) {
                collectionManager.saveDetails(mSwrl, details);
            }
        }
    }

    private void updateAvatarURL(CollectionManager collectionManager, Map<Integer, String> avatarCache, Swrl mSwrl) {
        int authorId = mSwrl.getAuthorId();
        Log.d(LOG_TAG, "Author ID: " + authorId);
        if (authorId != 0 && authorId != -1) {
            String newAvatarURL;
            if (avatarCache.containsKey(authorId)) {
                newAvatarURL = avatarCache.get(authorId);
            } else {
                newAvatarURL = SwrlUserHelpers.getUserAvatarURL(authorId);
                avatarCache.put(authorId, newAvatarURL);
            }
            Log.d(LOG_TAG, "AvatarURL: " + newAvatarURL);
            if (newAvatarURL != null) {
                collectionManager.updateAuthorAvatarURL(mSwrl, newAvatarURL);
            }
        }
    }

    private void setEmptyAuthorIdToSelfIfLoggedIn(CollectionManager collectionManager, Swrl mSwrl) {
        int userID = listActivity.preferences.getUserID();
        if ((mSwrl.getAuthorId() == -1 || mSwrl.getAuthorId() == 0 || mSwrl.getAuthor() == null)
                && userID != 0
                && userID != -1) {
            mSwrl.setAuthorId(userID);
            collectionManager.updateAuthorID(mSwrl, userID);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d(LOG_TAG, "Refreshing all details - finished");
        dialog.dismiss();
        listActivity.refreshList(true);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        dialog.setMessage(updateMessage + "\n\n" + values[0]);
    }

    @Override
    protected void onCancelled() {
        Log.d(LOG_TAG, "Refreshing all details - cancelled");
        dialog.dismiss();
    }
}
