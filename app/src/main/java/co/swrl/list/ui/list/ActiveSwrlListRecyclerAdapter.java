package co.swrl.list.ui.list;

import android.content.Context;
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
import co.swrl.list.ui.activity.ListActivity;

import static co.swrl.list.ui.activity.ViewActivity.ViewType.VIEW;


public class ActiveSwrlListRecyclerAdapter extends RecyclerView.Adapter implements SwrlListRecyclerAdapter {

    private final Context context;
    private final List<Swrl> swrls;
    private List<Swrl> cachedSwrls;
    private final ListActivity.DrawerListAdapter navListAdapter;
    private final ListActivity activity;
    private final CollectionManager collectionManager;

    public ActiveSwrlListRecyclerAdapter(ListActivity activity, CollectionManager collectionManager, ListActivity.DrawerListAdapter navListAdapter) {
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
        swrlRow.setProfileImage(swrl, context);
        swrlRow.setRowClickToOpenViewByType(position, swrls, context, VIEW);
    }

    @Override
    public int getItemCount() {
        return swrls.size();
    }

    @Override
    public int getSwrlCount() {
        return collectionManager.countActive();
    }

    @Override
    public int getSwrlCount(Type type) {
        return collectionManager.countActive(type);
    }

    @Override
    public void refreshList(Type type, boolean updateFromSource) {
        if (cachedSwrls == null || updateFromSource || cachedSwrls.size() != collectionManager.countActive()) {
            activity.showSpinner(true);
            activity.setBackgroundDimming(true);
            cachedSwrls = collectionManager.getActive();
        }
        List<Swrl> newSwrls = cachedSwrls;
        if (type != null && type != Type.UNKNOWN) {
            newSwrls = getFilteredSwrls(type);
        }
        swrls.clear();
        swrls.addAll(newSwrls);
        notifyDataSetChanged();
        navListAdapter.notifyDataSetChanged();
        activity.setNoSwrlsText();
        activity.setBackgroundDimming(false);
        activity.showSpinner(false);
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

    @Override
    public void swipeAction(RecyclerView.ViewHolder viewHolder, int position) {
        Swrl swrlToRemove = swrls.get(position);
        if (swrls.contains(swrlToRemove)) {
            swrls.remove(position);
            int cachePosition = cachedSwrls.indexOf(swrlToRemove);
            cachedSwrls.remove(swrlToRemove);
            collectionManager.markAsDone(swrlToRemove);
            notifyItemRemoved(position);
            navListAdapter.notifyDataSetChanged();
            activity.setNoSwrlsText();
            showUndoSnackbar(swrlToRemove, viewHolder.itemView, position, cachePosition);
        }
    }

    private void showUndoSnackbar(final Swrl swrl, View row, final int position, final int cachePosition) {
        String undoTitle = "\"" + swrl.getTitle() + "\" " + "marked as done";
        Snackbar.make(row, undoTitle, Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.clearAnimation();
                        reAddSwrl(position, swrl, cachePosition);
                    }
                }).show();
    }

    private void reAddSwrl(int position, Swrl swrl, int cachePosition) {
        swrls.add(position, swrl);
        cachedSwrls.add(cachePosition, swrl);
        collectionManager.markAsActive(swrl);
        notifyItemInserted(position);
        navListAdapter.notifyDataSetChanged();
        activity.setNoSwrlsText();
    }

}
