package co.swrl.list.ui.list;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import co.swrl.list.collection.CollectionManager;
import co.swrl.list.item.Swrl;

import static co.swrl.list.ui.activity.ViewActivity.ViewType.ADD;


public class AddSwrlResultsRecyclerAdapter extends RecyclerView.Adapter implements SwrlResultsRecyclerAdapter {

    private final Context context;
    private final List<Swrl> swrls = new ArrayList<>();
    private final CollectionManager collectionManager;

    public AddSwrlResultsRecyclerAdapter(Context context, CollectionManager collectionManager) {
        this.context = context;
        this.collectionManager = collectionManager;
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
        swrlRow.setAddButton(swrl, collectionManager, (Activity) context);
        swrlRow.setRowClickToOpenViewByType(position, swrls, context, ADD);
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
