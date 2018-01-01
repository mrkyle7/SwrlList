package co.swrl.list.ui.list.swrllists;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import co.swrl.list.collection.CollectionManager;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;
import co.swrl.list.item.actions.SwrlCoActions;
import co.swrl.list.ui.activity.ListActivity;
import co.swrl.list.ui.activity.RecommendationCreationActivity;
import co.swrl.list.ui.list.common.SwipeActions;
import co.swrl.list.ui.list.common.SwrlRow;
import co.swrl.list.ui.list.menus.DrawerListAdapter;

import static android.support.v4.content.ContextCompat.startActivity;
import static co.swrl.list.ui.activity.ViewActivity.ViewType.VIEW;


public class ActiveSwrlListRecyclerAdapter extends RecyclerView.Adapter implements SwrlListRecyclerAdapter {

    private final Context context;
    private final List<Swrl> swrls;
    private List<Swrl> cachedSwrls;
    private final DrawerListAdapter navListAdapter;
    private final ListActivity activity;
    private final CollectionManager collectionManager;

    public ActiveSwrlListRecyclerAdapter(ListActivity activity, CollectionManager collectionManager, DrawerListAdapter navListAdapter) {
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
    public void swipeLeftAction(RecyclerView.ViewHolder viewHolder, int position) {
        SwipeActions.CollectionManagerAction action = new SwipeActions.CollectionManagerAction() {
            @Override
            public void execute(Swrl swrl) {
                collectionManager.markAsDone(swrl);
            }
        };
        SwipeActions.CollectionManagerAction undoAction = new SwipeActions.CollectionManagerAction() {
            @Override
            public void execute(Swrl swrl) {
                collectionManager.markAsActive(swrl);
            }
        };
        SwipeActions.swipeAction(swrls, position, cachedSwrls, "marked as done", action, undoAction, SwrlCoActions.DONE, SwrlCoActions.LATER,
                activity, this, navListAdapter, viewHolder.itemView);
    }

    @Override
    public void swipeRightAction(RecyclerView.ViewHolder viewHolder, int position) {
        Swrl swrlToRecommend = swrls.get(position);
        Intent recommendationActivity = new Intent(activity, RecommendationCreationActivity.class);
        recommendationActivity.putExtra(RecommendationCreationActivity.EXTRAS_SWRL, swrlToRecommend);
        startActivity(activity, recommendationActivity, null);
    }
}
