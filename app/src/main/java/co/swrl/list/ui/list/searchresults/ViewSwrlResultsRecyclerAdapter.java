package co.swrl.list.ui.list.searchresults;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import co.swrl.list.collection.CollectionManager;
import co.swrl.list.item.Swrl;
import co.swrl.list.ui.list.common.SwrlRow;


public class ViewSwrlResultsRecyclerAdapter extends RecyclerView.Adapter implements SwrlResultsRecyclerAdapter {

    private final Activity activity;
    private final List<Swrl> swrls = new ArrayList<>();
    private final List<Swrl> orignalSwrls;
    private final Swrl originalSwrl;
    private final int originalPosition;
    private final CollectionManager collectionManager;

    public ViewSwrlResultsRecyclerAdapter(Activity activity, CollectionManager collectionManager, List<Swrl> originalSwrls, Swrl originalSwrl, int originalPosition) {
        this.activity = activity;
        this.collectionManager = collectionManager;
        this.orignalSwrls = originalSwrls;
        this.originalSwrl = originalSwrl;
        this.originalPosition = originalPosition;
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
        swrlRow.setImage(swrl, activity);
        swrlRow.setRowClickToReplaceViewWithDetails(swrl, originalSwrl, orignalSwrls, originalPosition, collectionManager, activity);
    }

    @Override
    public int getItemCount() {
        return swrls.size();
    }


    @Override
    public void clear() {
        swrls.clear();
        notifyDataSetChanged();
    }

    @Override
    public void addAll(List<Swrl> results) {
        swrls.clear();
        swrls.addAll(results);
        notifyDataSetChanged();
    }
}
