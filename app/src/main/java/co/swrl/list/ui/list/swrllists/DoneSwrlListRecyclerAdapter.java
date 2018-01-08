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
import static co.swrl.list.ui.activity.ViewActivity.ViewType.DONE;


public class DoneSwrlListRecyclerAdapter extends RecyclerView.Adapter implements SwrlListRecyclerAdapter {

    private final Context context;
    private final List<Swrl> swrls;
    private List<Swrl> cachedSwrls;
    private final DrawerListAdapter navListAdapter;
    private final ListActivity activity;
    private final CollectionManager collectionManager;

    public DoneSwrlListRecyclerAdapter(ListActivity activity, CollectionManager collectionManager, DrawerListAdapter navListAdapter) {
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
        int firstPage = (position - 50 <= 0) ? 0 : position - 50;
        int lastPage = firstPage + 100 >= swrls.size() ? swrls.size() : firstPage + 100;
        ArrayList<Swrl> swrlsToPage = new ArrayList<>();
        if (swrls.size() != 0) {
            swrlsToPage.addAll(swrls.subList(firstPage, lastPage));
        }
        int pagePosition = swrlsToPage.indexOf(swrl);
        swrlRow.setRowClickToOpenViewByType(pagePosition, swrlsToPage, context, DONE);
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
    public void refreshList(Type type, String textFilter, boolean updateFromSource) {
        if (cachedSwrls == null || updateFromSource || cachedSwrls.size() != collectionManager.countDone()) {
            activity.showSpinner(true);
            activity.setBackgroundDimming(true);
            cachedSwrls = collectionManager.getDone();
        }
        List<Swrl> newSwrls = cachedSwrls;
        if (typeFilterSet(type) || !textFilter.isEmpty()) {
            newSwrls = getFilteredSwrls(type, textFilter);
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

    private boolean typeFilterSet(Type type) {
        return type != null && type != Type.UNKNOWN;
    }

    @Override
    public void swipeLeftAction(RecyclerView.ViewHolder viewHolder, int position) {
        SwipeActions.CollectionManagerAction action = new SwipeActions.CollectionManagerAction() {
            @Override
            public void execute(Swrl swrl) {
                collectionManager.permanentlyDelete(swrl);
            }
        };
        SwipeActions.CollectionManagerAction undoAction = new SwipeActions.CollectionManagerAction() {
            @Override
            public void execute(Swrl swrl) {
                collectionManager.save(swrl);
                collectionManager.markAsDone(swrl);
            }
        };
        SwipeActions.swipeAction(swrls, position, cachedSwrls, "deleted", action, undoAction, SwrlCoActions.DISMISSED, SwrlCoActions.DONE,
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
