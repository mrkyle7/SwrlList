package co.swrl.list.ui.list;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import co.swrl.list.collection.CollectionManager;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;
import co.swrl.list.ui.activity.ListActivity;


public class SwrlListRecyclerAdapter extends RecyclerView.Adapter {

    private final Context context;
    private List<Swrl> swrls;
    private final ListActivity.DrawerListAdapter navListAdapter;
    private final ListActivity activity;
    private final CollectionManager collectionManager;

    public SwrlListRecyclerAdapter(ListActivity activity, CollectionManager collectionManager, ListActivity.DrawerListAdapter navListAdapter) {
        this.activity = activity;
        this.context = this.activity.getApplicationContext();
        this.collectionManager = collectionManager;
        swrls = collectionManager.getActive();
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
        swrlRow.setRowClickToOpenView(position, swrls, context);
    }

    @Override
    public int getItemCount() {
        return swrls.size();
    }

    public List<Swrl> getSwrls(){
        return swrls;
    }

    public void updateSwrl(Swrl swrl){
        int currentPosition = swrls.indexOf(swrl);
        if (currentPosition != -1){
            swrls.set(currentPosition, swrl);
            notifyItemChanged(currentPosition);
        }
    }

    public void refreshAll() {
        List<Swrl> active = collectionManager.getActive();
        swrls.clear();
        swrls.addAll(active);
        notifyDataSetChanged();
        navListAdapter.notifyDataSetChanged();
        activity.setNoSwrlsText();
    }

    public void refreshAllWithFilter(Type type) {
        List<Swrl> filtered = collectionManager.getActiveWithFilter(type);
        swrls.clear();
        swrls.addAll(filtered);
        notifyDataSetChanged();
        navListAdapter.notifyDataSetChanged();
        activity.setNoSwrlsText();
    }

    public void markAsDone(RecyclerView.ViewHolder viewHolder, int position) {
        Swrl swrlToRemove = swrls.get(position);
        if (swrls.contains(swrlToRemove)) {
            swrls.remove(position);
            collectionManager.markAsDone(swrlToRemove);
            notifyItemRemoved(position);
            navListAdapter.notifyDataSetChanged();
            activity.setNoSwrlsText();
            showUndoSnackbar(swrlToRemove, viewHolder.itemView, position);
        }
    }

    private void showUndoSnackbar(final Swrl swrl, View row, final int position) {
        String undoTitle = "\"" + swrl.getTitle() + "\" " + "marked as done";
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
        collectionManager.markAsActive(swrl);
        notifyItemInserted(position);
        navListAdapter.notifyDataSetChanged();
        activity.setNoSwrlsText();
    }

}
