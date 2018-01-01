package co.swrl.list.tasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import co.swrl.list.collection.CollectionManager;
import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.actions.SwrlCoActions;
import co.swrl.list.ui.activity.ListActivity;

public class SyncAllSwrlsTask extends AsyncTask<Void, String, Void> {
    private static final String LOG_TAG = "SyncAllSwrlsTask";
    private ListActivity listActivity;
    private String updateMessage = "Syncing all Swrls." +
            "\nThis may take a few minutes!";
    private AlertDialog dialog;
    private AsyncTask mTask;

    public SyncAllSwrlsTask(ListActivity listActivity, final Context context) {
        this.listActivity = listActivity;
        this.dialog = new ProgressDialog(context);
        this.mTask = this;
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
        Log.d(LOG_TAG, "Syncing all swrls");
        CollectionManager collectionManager = new SQLiteCollectionManager(listActivity.getApplicationContext());
        backUpSwrls((ArrayList<?>) collectionManager.getActive(), "Backing up Active swrls", SwrlCoActions.LATER, collectionManager);
        backUpSwrls((ArrayList<?>) collectionManager.getDone(), "Backing up Done swrls", SwrlCoActions.DONE, collectionManager);
        return null;
    }

    private void backUpSwrls(ArrayList<?> swrlsFromDB, String publishMessage, String response, CollectionManager collectionManager) {
        ArrayList<Swrl> swrlsToProcess = new ArrayList<>();
        for (Object swrl : swrlsFromDB) {
            Swrl mSwrl = (Swrl) swrl;
            Log.d(LOG_TAG, mSwrl.toString() + " id: " + mSwrl.getId());
            if (mSwrl.getId() == 0) swrlsToProcess.add(mSwrl);
        }
        int totalToProcess = swrlsToProcess.size();
        for (Swrl swrl : swrlsToProcess) {
            publishProgress(publishMessage
                    + "\n"
                    + String.valueOf(swrlsToProcess.indexOf(swrl) + 1)
                    + " of "
                    + String.valueOf(totalToProcess)
                    + "...");
            SwrlCoActions.create(swrl, response, listActivity.preferences, collectionManager);
            SwrlCoActions.create(swrl, response, listActivity.preferences, collectionManager);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d(LOG_TAG, "Syncing all Swrls - finished");
        dialog.dismiss();
        listActivity.refreshList(true);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        dialog.setMessage(updateMessage + "\n\n" + values[0]);
    }

    @Override
    protected void onCancelled() {
        Log.d(LOG_TAG, "Syncing all Swrls - cancelled");
        dialog.dismiss();
    }
}
