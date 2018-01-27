package co.swrl.list.ui.list.common;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import java.util.List;

import co.swrl.list.R;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.actions.SwrlCoActions;
import co.swrl.list.ui.activity.ListActivity;
import co.swrl.list.ui.list.menus.DrawerListAdapter;
import co.swrl.list.utils.SwrlDialogs;
import co.swrl.list.utils.SwrlPreferences;

public class SwipeActions {
    private static final String LOG_TAG = "SWIPE_ACTIONS";

    private SwipeActions() {
    }

    public interface CollectionManagerAction {
        void execute(Swrl swrl);
    }

    public static void swipeAction(List<Swrl> swrls, int position, List<Swrl> cachedSwrls, String actionTitle, CollectionManagerAction collectionManagerAction, CollectionManagerAction undoAction,
                                   final String swrlCoResponse, String undoSwrlCoResponse, ListActivity activity,
                                   RecyclerView.Adapter adapter, DrawerListAdapter navListAdapter) {
        Swrl swrlToAction = swrls.get(position);
        int cachePosition = cachedSwrls.indexOf(swrlToAction);
        final SwrlPreferences preferences = new SwrlPreferences(activity);
        if (swrls.contains(swrlToAction)) {
            swrls.remove(swrlToAction);
            cachedSwrls.remove(swrlToAction);
            collectionManagerAction.execute(swrlToAction);
            if (preferences.loggedIn()) {
                new AsyncTask<Swrl, Void, Void>() {
                    @Override
                    protected Void doInBackground(Swrl... swrls) {
                        Swrl mSwrl = swrls[0];
                        SwrlCoActions.respond(mSwrl, swrlCoResponse, preferences, null);
                        return null;
                    }
                }.execute(swrlToAction);
            }
            adapter.notifyItemRemoved(position);
            navListAdapter.notifyDataSetChanged();
            activity.setNoSwrlsText();
            SwrlDialogs.showReviewPopUp(swrlToAction, preferences, activity);
            showUndoSnackbar(swrlToAction, swrls, position, cachedSwrls, cachePosition,
                    undoAction, undoSwrlCoResponse, actionTitle,
                    adapter, navListAdapter,
                    activity, preferences, activity.findViewById(R.id.snackbar));
        }
    }

    private static void showUndoSnackbar(final Swrl swrl, final List<Swrl> swrls, final int position, final List<Swrl> cachedSwrls, final int cachePosition,
                                         final CollectionManagerAction undoAction, final String undoSwrlCoResponse, String actionTitle,
                                         final RecyclerView.Adapter adapter, final DrawerListAdapter navListAdapter,
                                         final ListActivity activity, final SwrlPreferences preferences, View row) {
        String undoTitle = "\"" + swrl.getTitle() + "\" " + actionTitle;
        Snackbar.make(row, undoTitle, Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.clearAnimation();
                        undoAction(position, swrl, swrls, cachedSwrls, cachePosition, preferences, undoAction, adapter, navListAdapter, activity, undoSwrlCoResponse);
                    }
                }).show();
    }

    private static void undoAction(int position, Swrl swrl, List<Swrl> swrls, List<Swrl> cachedSwrls, int cachePosition, final SwrlPreferences preferences, CollectionManagerAction collectionManagerUndoCollectionManagerAction,
                                   RecyclerView.Adapter adapter, DrawerListAdapter navListAdapter, ListActivity activity, final String undoSwrlCoResponse) {
        swrls.add(position, swrl);
        cachedSwrls.add(cachePosition, swrl);
        collectionManagerUndoCollectionManagerAction.execute(swrl);
        if (preferences.loggedIn()) {
            new AsyncTask<Swrl, Void, Void>() {
                @Override
                protected Void doInBackground(Swrl... swrls) {
                    Swrl mSwrl = swrls[0];
                    SwrlCoActions.respond(mSwrl, undoSwrlCoResponse, preferences, null);
                    return null;
                }
            }.execute(swrl);
        }
        adapter.notifyItemInserted(position);
        navListAdapter.notifyDataSetChanged();
        activity.setNoSwrlsText();
    }

    private static int getDPI(int i, View rootView) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, rootView.getContext().getResources().getDisplayMetrics());
    }
}
