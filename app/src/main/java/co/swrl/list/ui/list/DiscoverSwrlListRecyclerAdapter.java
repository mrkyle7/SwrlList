package co.swrl.list.ui.list;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import co.swrl.list.collection.CollectionManager;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;
import co.swrl.list.item.discovery.PublicSwrls;
import co.swrl.list.ui.activity.ListActivity;

import static co.swrl.list.ui.activity.ViewActivity.ViewType.ADD_DISCOVER;


public class DiscoverSwrlListRecyclerAdapter extends RecyclerView.Adapter implements SwrlListRecyclerAdapter {

    private final Context context;
    private final List<Swrl> swrls;
    private List<Swrl> cachedSwrls;
    private final ListActivity.DrawerListAdapter navListAdapter;
    private final ListActivity activity;
    private final CollectionManager collectionManager;
    private AsyncTask<Void, Void, List<Swrl>> backgroundSearch;

    public DiscoverSwrlListRecyclerAdapter(ListActivity activity, CollectionManager collectionManager, ListActivity.DrawerListAdapter navListAdapter) {
        this.activity = activity;
        this.context = this.activity.getApplicationContext();
        this.collectionManager = collectionManager;
        swrls = new ArrayList<>();
        this.navListAdapter = navListAdapter;
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
        int firstPage = (position - 50 <= 0) ? 0 : position - 50;
        int lastPage = firstPage + 100 >= swrls.size() - 1 ? swrls.size() - 1 : firstPage + 100;
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
    public List<Swrl> getSwrls() {
        return swrls;
    }

    @Override
    public void refreshAll() {
        cancelExistingSearches();
        backgroundSearch = new AsyncTask<Void, Void, List<Swrl>>() {
            @Override
            protected void onPreExecute() {
                spinWhenGettingSwrls();
            }

            @Override
            protected List<Swrl> doInBackground(Void... voids) {
                return new PublicSwrls().get();
            }

            @Override
            protected void onPostExecute(List<Swrl> discoveredSwrls) {
                cachedSwrls = discoveredSwrls;
                removeSpinner();
                updateList(discoveredSwrls);
            }

            @Override
            protected void onCancelled() {
                if (backgroundSearch == null || backgroundSearch.isCancelled()) removeSpinner();
            }
        };
        backgroundSearch.execute();
    }

    @Override
    public void refreshAllWithFilter(final Type type) {
        cancelExistingSearches();
        if (cachedSwrls == null) {
            backgroundSearch = new AsyncTask<Void, Void, List<Swrl>>() {
                @Override
                protected void onPreExecute() {
                    spinWhenGettingSwrls();
                }

                @Override
                protected List<Swrl> doInBackground(Void... voids) {
                    return new PublicSwrls().get();
                }

                @Override
                protected void onPostExecute(List<Swrl> discoveredSwrls) {
                    cachedSwrls = discoveredSwrls;
                    List<Swrl> filtered = getFilteredSwrls(type);
                    removeSpinner();
                    updateList(filtered);
                }

                @Override
                protected void onCancelled() {
                    if (backgroundSearch == null || backgroundSearch.isCancelled()) removeSpinner();
                }
            };
            backgroundSearch.execute();
        } else {
            List<Swrl> filtered = getFilteredSwrls(type);
            updateList(filtered);
        }
    }

    public void cancelExistingSearches() {
        if (backgroundSearch != null){
            backgroundSearch.cancel(true);
        }
    }

    @NonNull
    private List<Swrl> getFilteredSwrls(Type type) {
        List<Swrl> filtered = new ArrayList<>();
        for (Swrl swrl : cachedSwrls) {
            if (swrl.getType() == type) {
                filtered.add(swrl);
            }
        }
        return filtered;
    }


    private void removeSpinner() {
        activity.setBackgroundDimming(false);
        activity.showSpinner(false);
    }

    private void updateList(List<Swrl> updatedSwrls) {
        swrls.clear();
        swrls.addAll(updatedSwrls);
        notifyDataSetChanged();
        navListAdapter.notifyDataSetChanged();
        activity.setNoSwrlsText();
    }

    private void spinWhenGettingSwrls() {
        activity.setBackgroundDimming(true);
        activity.showSpinner(true);
    }

    @Override
    public void swipeAction(RecyclerView.ViewHolder viewHolder, int position) {
        Swrl swrlToAdd = swrls.get(position);
        if (swrls.contains(swrlToAdd)) {
            swrls.remove(position);
            collectionManager.save(swrlToAdd);
            notifyItemRemoved(position);
            navListAdapter.notifyDataSetChanged();
            activity.setNoSwrlsText();
            showUndoSnackbar(swrlToAdd, viewHolder.itemView, position);
        }
    }

    private void showUndoSnackbar(final Swrl swrl, View row, final int position) {
        String undoTitle = "\"" + swrl.getTitle() + "\" " + "added";
        Snackbar.make(row, undoTitle, Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.clearAnimation();
                        reAddSwrl(position, swrl);
                    }
                }).show();
    }

    private void reAddSwrl(int position, Swrl swrl) {
        swrls.add(position, swrl);
        collectionManager.permanentlyDelete(swrl);
        notifyItemInserted(position);
        navListAdapter.notifyDataSetChanged();
        activity.setNoSwrlsText();
    }

}
