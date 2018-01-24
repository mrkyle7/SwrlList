package co.swrl.list.tasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import co.swrl.list.collection.CollectionManager;
import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.actions.SwrlCoActions;
import co.swrl.list.item.discovery.SwrlCoUserLists;
import co.swrl.list.ui.activity.ListActivity;

import static co.swrl.list.item.discovery.SwrlCoUserLists.currentUserLists;

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
        final CollectionManager collectionManager = new SQLiteCollectionManager(listActivity.getApplicationContext());
        backUpSwrls(collectionManager.getActive(), "Backing up Active swrls", SwrlCoActions.LATER, collectionManager);
        backUpSwrls(collectionManager.getDone(), "Backing up Done swrls", SwrlCoActions.DONE, collectionManager);

        SwrlCoUserLists.UserLists userLists = currentUserLists(listActivity.preferences).get();
        List<Swrl> processed = new ArrayList<>();
        updateLocalResponse(collectionManager.getActive(), "Syncing local Active swrls", SwrlCoActions.LATER, collectionManager, processed, userLists.active);
        updateLocalResponse(collectionManager.getDone(), "Syncing local Done swrls", SwrlCoActions.DONE, collectionManager, processed, userLists.done);
        updateLocalResponse(collectionManager.getDismissed(), "Syncing local Dismissed swrls", SwrlCoActions.DISMISSED, collectionManager, processed, userLists.dismissed);

        updateSwrlCoResponse(collectionManager.getActive(), userLists.active, "Syncing Swrl Co Active swrls", new CollectionManagerOperation() {
            @Override
            public void execute(Swrl swrl) {
                Log.d(LOG_TAG, "saving " + swrl.toString() + " id: " + swrl.getId());
                collectionManager.save(swrl);
            }
        }, processed);

        updateSwrlCoResponse(collectionManager.getDone(), userLists.done, "Syncing Swrl Co Done swrls", new CollectionManagerOperation() {
            @Override
            public void execute(Swrl swrl) {
                collectionManager.save(swrl);
                collectionManager.markAsDone(swrl);
            }
        }, processed);
        updateSwrlCoResponse(collectionManager.getDismissed(), userLists.dismissed, "Syncing Swrl Co Dismissed swrls", new CollectionManagerOperation() {
            @Override
            public void execute(Swrl swrl) {
                collectionManager.permanentlyDelete(swrl);
            }
        }, processed);
        updateSwrlCoResponse(collectionManager.getSwrled(), userLists.swrled, "Syncing Swrl Co Swrled swrls", new CollectionManagerOperation() {
            @Override
            public void execute(Swrl swrl) {
                collectionManager.saveRecommendation(swrl);
            }
        }, processed);
        return null;
    }

    private void backUpSwrls(List<Swrl> swrlsFromDB, String publishMessage, String response, CollectionManager collectionManager) {
        ArrayList<Swrl> swrlsToProcess = new ArrayList<>();
        for (Swrl swrl : swrlsFromDB) {
            if (swrl.getId() == 0) {
                Log.d(LOG_TAG, publishMessage + swrl.toString() + " id: " + swrl.getId());
                swrlsToProcess.add(swrl);
            }
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
        }
    }

    private void updateLocalResponse(List<Swrl> swrlsFromDB, String publishMessage, String response, CollectionManager collectionManager, List<Swrl> processed, List<Swrl> ignore) {
        ArrayList<Swrl> swrlsToProcess = new ArrayList<>();
        for (Swrl swrl : swrlsFromDB) {
            if (ignore != null && !ignore.contains(swrl)) {
                Log.d(LOG_TAG, publishMessage + swrl.toString() + " id: " + swrl.getId());
                swrlsToProcess.add(swrl);
            }
        }
        int totalToProcess = swrlsToProcess.size();
        for (Swrl swrl : swrlsToProcess) {
            publishProgress(publishMessage
                    + "\n"
                    + String.valueOf(swrlsToProcess.indexOf(swrl) + 1)
                    + " of "
                    + String.valueOf(totalToProcess)
                    + "...");
            SwrlCoActions.respond(swrl, response, listActivity.preferences, collectionManager);
            processed.add(swrl);
        }
    }

    private interface CollectionManagerOperation {
        void execute(Swrl swrl);
    }

    private void updateSwrlCoResponse(List<Swrl> swrlsFromDB, List<Swrl> swrlsFromSwrlCo, String publishMessage, CollectionManagerOperation operation, List<Swrl> processed) {
        if (swrlsFromSwrlCo == null) return;
        ArrayList<Swrl> swrlsToProcess = new ArrayList<>();
        for (Swrl swrl : swrlsFromSwrlCo) {
            if (!swrlsFromDB.contains(swrl) && !processed.contains(swrl)) {
                Log.d(LOG_TAG, publishMessage + swrl.toString() + " id: " + swrl.getId());
                swrlsToProcess.add(swrl);
            }
        }
        int totalToProcess = swrlsToProcess.size();
        for (Swrl swrl : swrlsToProcess) {
            publishProgress(publishMessage
                    + "\n"
                    + String.valueOf(swrlsToProcess.indexOf(swrl) + 1)
                    + " of "
                    + String.valueOf(totalToProcess)
                    + "...");
            operation.execute(swrl);
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
