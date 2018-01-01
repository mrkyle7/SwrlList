package co.swrl.list.ui.list.swrllists;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import co.swrl.list.collection.CollectionManager;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;
import co.swrl.list.item.actions.SwrlCoActions;
import co.swrl.list.item.discovery.SwrlCoLists;
import co.swrl.list.ui.activity.ListActivity;
import co.swrl.list.ui.list.common.SwipeActions;
import co.swrl.list.ui.list.common.SwrlRow;
import co.swrl.list.ui.list.menus.DrawerListAdapter;
import co.swrl.list.utils.SwrlPreferences;

import static co.swrl.list.item.discovery.SwrlCoLists.inboxSwrls;
import static co.swrl.list.item.discovery.SwrlCoLists.publicSwrls;
import static co.swrl.list.item.discovery.SwrlCoLists.weightedSwrls;
import static co.swrl.list.ui.activity.ViewActivity.ViewType.ADD_DISCOVER;


public class DiscoverSwrlListRecyclerAdapter extends RecyclerView.Adapter implements SwrlListRecyclerAdapter {

    private static final String LOG_TAG = "DISCOVER_ADAPTER";
    private final Context context;
    private final List<Swrl> swrls;
    private List<Swrl> cachedSwrls;
    private final DrawerListAdapter navListAdapter;
    private final ListActivity activity;
    private final SwrlCoLists swrlGetter;
    private final CollectionManager collectionManager;
    private AsyncTask<Void, Void, List<Swrl>> backgroundSearch;

    private DiscoverSwrlListRecyclerAdapter(ListActivity activity, CollectionManager collectionManager, DrawerListAdapter navListAdapter, SwrlCoLists swrlGetter) {
        this.activity = activity;
        this.swrlGetter = swrlGetter;
        this.context = this.activity.getApplicationContext();
        this.collectionManager = collectionManager;
        swrls = new ArrayList<>();
        this.navListAdapter = navListAdapter;
    }

    public static DiscoverSwrlListRecyclerAdapter publicDiscover(ListActivity activity, CollectionManager collectionManager, DrawerListAdapter navListAdapter) {
        return new DiscoverSwrlListRecyclerAdapter(activity, collectionManager, navListAdapter, publicSwrls(collectionManager));
    }

    public static DiscoverSwrlListRecyclerAdapter weightedDiscover(ListActivity activity, CollectionManager collectionManager, DrawerListAdapter navListAdapter, SwrlPreferences preferences) {
        return new DiscoverSwrlListRecyclerAdapter(activity, collectionManager, navListAdapter, weightedSwrls(collectionManager, preferences));
    }

    public static DiscoverSwrlListRecyclerAdapter inboxDiscover(ListActivity activity, CollectionManager collectionManager, DrawerListAdapter navListAdapter, SwrlPreferences preferences) {
        return new DiscoverSwrlListRecyclerAdapter(activity, collectionManager, navListAdapter, inboxSwrls(collectionManager, preferences));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SwrlRow(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SwrlRow swrlRow = (SwrlRow) holder;
        Swrl swrl = swrls.get(position);

        swrlRow.setTitle(swrl);
        swrlRow.setSubTitle(swrl);
        swrlRow.setSubtitle2(swrl);
        swrlRow.setImage(swrl, context);
        swrlRow.setProfileImage(swrl, context);
        int firstPage = (position - 50 <= 0) ? 0 : position - 50;
        int lastPage = firstPage + 100 >= swrls.size() ? swrls.size() : firstPage + 100;
        ArrayList<Swrl> swrlsToPage = new ArrayList<>();
        if (swrls.size() != 0) {
            swrlsToPage.addAll(swrls.subList(firstPage, lastPage));
        }
        int pagePosition = swrlsToPage.indexOf(swrl);
        swrlRow.setRowClickToOpenViewByType(pagePosition, swrlsToPage, context, ADD_DISCOVER);
    }

    @Override
    public int getItemCount() {
        return swrls.size();
    }

    @Override
    public int getSwrlCount() {
        return cachedSwrls == null ? 0 : cachedSwrls.size();
    }

    @Override
    public int getSwrlCount(Type type) {
        if (cachedSwrls == null) return 0;
        int i = 0;
        for (Swrl swrl : cachedSwrls) {
            if (swrl.getType() == type) {
                i++;
            }
        }
        return i;
    }

    @Override
    public void refreshList(final Type type, final String textFilter, boolean updateFromSource) {
        if (type == null) {
            Log.d(LOG_TAG, "Refresh All with no filter");
        } else {
            Log.d(LOG_TAG, "Refresh All with filter: " + type.getFriendlyName());
        }
        cancelExistingSearches();
        if (cachedSwrls == null || updateFromSource) {
            backgroundSearch = new AsyncTask<Void, Void, List<Swrl>>() {
                @Override
                protected void onPreExecute() {
                    setSpinner(true);
                }

                @Override
                protected List<Swrl> doInBackground(Void... voids) {
                    return swrlGetter.get();
                }

                @Override
                protected void onPostExecute(List<Swrl> discoveredSwrls) {
                    cachedSwrls = discoveredSwrls;
                    updateAdapter(type, textFilter);
                }

                @Override
                protected void onCancelled() {
                    if (backgroundSearch == null || backgroundSearch.isCancelled()) setSpinner(false);
                }
            };
            backgroundSearch.execute();
        } else {
            updateAdapter(type, textFilter);
        }
    }

    private void updateAdapter(Type type, String textFilter) {
        List<Swrl> newSwrls = cachedSwrls;
        if ((type != null && type != Type.UNKNOWN) || !textFilter.isEmpty()) {
            newSwrls = getFilteredSwrls(type, textFilter);
        }
        setSpinner(false);
        swrls.clear();
        swrls.addAll(newSwrls);
        notifyDataSetChanged();
        navListAdapter.notifyDataSetChanged();
        activity.setNoSwrlsText();
    }

    private boolean typeFilterSet(Type type) {
        return type != null && type != Type.UNKNOWN;
    }

    public void cancelExistingSearches() {
        if (backgroundSearch != null) {
            backgroundSearch.cancel(true);
        }
    }

    @NonNull
    private List<Swrl> getFilteredSwrls(Type type, String textFilter) {
        List<Swrl> filtered = new ArrayList<>();
        textFilter = textFilter.toLowerCase();
        for (Swrl swrl : cachedSwrls) {
            String swrlTextToSearch;
            if (swrl.getDetails() != null) {
                swrlTextToSearch = swrl.getTitle() + swrl.getAuthor() + swrl.getDetails().toString();
            } else {
                swrlTextToSearch = swrl.getTitle() + swrl.getAuthor();
            }
            if ((swrl.getType() == type || !typeFilterSet(type)) && swrlTextToSearch.toLowerCase().contains(textFilter)) {
                filtered.add(swrl);
            }
        }
        return filtered;
    }

    private void setSpinner(boolean spinning){
        activity.setBackgroundDimming(spinning);
        activity.showSpinner(spinning);
    }

    @Override
    public void swipeLeftAction(RecyclerView.ViewHolder viewHolder, int position) {
        SwipeActions.CollectionManagerAction action = new SwipeActions.CollectionManagerAction() {
            @Override
            public void execute(Swrl swrl) {
                collectionManager.markAsDismissed(swrl);
            }
        };
        SwipeActions.CollectionManagerAction undoAction = new SwipeActions.CollectionManagerAction() {
            @Override
            public void execute(Swrl swrl) {
                collectionManager.permanentlyDelete(swrl);
            }
        };
        SwipeActions.swipeAction(swrls, position, cachedSwrls, "dismissed", action, undoAction, SwrlCoActions.DISMISSED, SwrlCoActions.REMOVE_RESPONSE,
                activity, this, navListAdapter, viewHolder.itemView);
    }

    @Override
    public void swipeRightAction(RecyclerView.ViewHolder viewHolder, int position) {
        SwipeActions.CollectionManagerAction action = new SwipeActions.CollectionManagerAction() {
            @Override
            public void execute(Swrl swrl) {
                collectionManager.save(swrl);
            }
        };
        SwipeActions.CollectionManagerAction undoAction = new SwipeActions.CollectionManagerAction() {
            @Override
            public void execute(Swrl swrl) {
                collectionManager.permanentlyDelete(swrl);
            }
        };
        SwipeActions.swipeAction(swrls, position, cachedSwrls, "added", action, undoAction, SwrlCoActions.LATER, SwrlCoActions.REMOVE_RESPONSE,
                activity, this, navListAdapter, viewHolder.itemView);
    }
}
