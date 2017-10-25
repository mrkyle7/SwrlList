package co.swrl.list.ui.list;

import android.content.Context;
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

import static co.swrl.list.ui.activity.ViewActivity.ViewType.DONE;


public class DoneSwrlListRecyclerAdapter extends RecyclerView.Adapter implements SwrlListRecyclerAdapter {

    private final Context context;
    private List<Swrl> swrls;
    private final ListActivity.DrawerListAdapter navListAdapter;
    private final ListActivity activity;
    private final CollectionManager collectionManager;

    public DoneSwrlListRecyclerAdapter(ListActivity activity, CollectionManager collectionManager, ListActivity.DrawerListAdapter navListAdapter) {
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
        swrlRow.setRowClickToOpenViewByType(position, swrls, context, DONE);
    }

    @Override
    public int getItemCount() {
        return swrls.size();
    }

    @Override
    public int getSwrlCount() {
        return collectionManager.countDone();
    }

    @Override
    public int getSwrlCount(Type type) {
        return collectionManager.countDone(type);
    }

    @Override
    public List<Swrl> getSwrls() {
        return swrls;
    }

    @Override
    public void refreshAll() {
        List<Swrl> done = collectionManager.getDone();
        swrls.clear();
        swrls.addAll(done);
        notifyDataSetChanged();
    }

    @Override
    public void refreshAllWithFilter(Type type) {
        List<Swrl> filtered = collectionManager.getDone(type);
        swrls.clear();
        swrls.addAll(filtered);
        notifyDataSetChanged();
    }

    @Override
    public void swipeAction(RecyclerView.ViewHolder viewHolder, int position) {
        Swrl swrlToRemove = swrls.get(position);
        if (swrls.contains(swrlToRemove)) {
            swrls.remove(position);
            collectionManager.permanentlyDelete(swrlToRemove);
            notifyItemRemoved(position);
            navListAdapter.notifyDataSetChanged();
            activity.setNoSwrlsText();
            showUndoSnackbar(swrlToRemove, viewHolder.itemView, position);
        }
    }

    private void showUndoSnackbar(final Swrl swrl, View row, final int position) {
        String undoTitle = "\"" + swrl.getTitle() + "\" " + "deleted";
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
        collectionManager.save(swrl);
        collectionManager.markAsDone(swrl);
        notifyItemInserted(position);
        navListAdapter.notifyDataSetChanged();
        activity.setNoSwrlsText();
    }

}
