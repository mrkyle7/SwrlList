package co.swrl.list.ui.list.swrllists;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import co.swrl.list.collection.CollectionManager;
import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.SwrlListGenerator;
import co.swrl.list.item.Type;
import co.swrl.list.item.actions.SwrlCoActions;
import co.swrl.list.ui.activity.ListActivity;
import co.swrl.list.ui.activity.RecommendationCreationActivity;
import co.swrl.list.ui.activity.ViewActivity;
import co.swrl.list.ui.list.common.SwipeActions;
import co.swrl.list.ui.list.common.SwrlRow;
import co.swrl.list.ui.list.menus.DrawerListAdapter;
import co.swrl.list.utils.SwrlPreferences;

import static android.support.v4.content.ContextCompat.startActivity;
import static co.swrl.list.item.discovery.SwrlCoLists.inboxSwrls;
import static co.swrl.list.item.discovery.SwrlCoLists.publicSwrls;
import static co.swrl.list.item.discovery.SwrlCoLists.weightedSwrls;
import static co.swrl.list.ui.activity.ViewActivity.ViewType.ADD_DISCOVER;
import static co.swrl.list.ui.activity.ViewActivity.ViewType.DONE;
import static co.swrl.list.ui.activity.ViewActivity.ViewType.VIEW;


public class SwrlListRecyclerAdapterFactory extends RecyclerView.Adapter implements SwrlListRecyclerAdapter {

    private final String LOG_TAG;
    private final Context context;
    private final List<Swrl> swrls;
    private final ViewActivity.ViewType viewType;
    private List<Swrl> cachedSwrls;
    private final DrawerListAdapter navListAdapter;
    private final ListActivity activity;
    private final SwrlListGenerator swrlListGenerator;
    private final CollectionManager collectionManager;
    private AsyncTask<Void, Void, List<Swrl>> backgroundGetter;
    private SwipeAction swipeLeftAction;
    private SwipeAction swipeRightAction;
    private ShouldRefresh shouldRefresh;

    private interface SwipeAction {
        void execute(final CollectionManager collectionManager, List<Swrl> swrls, List<Swrl> cachedSwrls,
                     SwrlListRecyclerAdapterFactory adapter, ListActivity activity, DrawerListAdapter navListAdapter,
                     RecyclerView.ViewHolder viewHolder, int position);
    }

    private interface ShouldRefresh {
        boolean check(CollectionManager collectionManager, List<Swrl> cachedSwrls);
    }

    private static final SwipeAction discoverLeftAction = new SwipeAction() {
        @Override
        public void execute(final CollectionManager collectionManager, List<Swrl> swrls, List<Swrl> cachedSwrls,
                            SwrlListRecyclerAdapterFactory adapter, ListActivity activity, DrawerListAdapter navListAdapter,
                            RecyclerView.ViewHolder viewHolder, int position) {
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
                    activity, adapter, navListAdapter, viewHolder.itemView);
        }
    };

    private static final SwipeAction discoverRightAction = new SwipeAction() {
        @Override
        public void execute(final CollectionManager collectionManager, List<Swrl> swrls, List<Swrl> cachedSwrls,
                            SwrlListRecyclerAdapterFactory adapter, ListActivity activity, DrawerListAdapter navListAdapter,
                            RecyclerView.ViewHolder viewHolder, int position) {
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
                    activity, adapter, navListAdapter, viewHolder.itemView);
        }
    };

    private static final ShouldRefresh discoverRefreshCheck = new ShouldRefresh() {
        @Override
        public boolean check(CollectionManager collectionManager, List<Swrl> cachedSwrls) {
            return false;
        }
    };

    private static final SwipeAction recommendSwipeAction = new SwipeAction() {
        @Override
        public void execute(CollectionManager collectionManager, List<Swrl> swrls, List<Swrl> cachedSwrls, SwrlListRecyclerAdapterFactory adapter, ListActivity activity, DrawerListAdapter navListAdapter, RecyclerView.ViewHolder viewHolder, int position) {
            Swrl swrlToRecommend = swrls.get(position);
            Intent recommendationActivity = new Intent(activity, RecommendationCreationActivity.class);
            recommendationActivity.putExtra(RecommendationCreationActivity.EXTRAS_SWRL, swrlToRecommend);
            startActivity(activity, recommendationActivity, null);
        }
    };


    private SwrlListRecyclerAdapterFactory(String log_tag, ListActivity activity, CollectionManager collectionManager, DrawerListAdapter navListAdapter, SwrlListGenerator swrlListGenerator, ViewActivity.ViewType viewType, SwipeAction swipeLeftAction, SwipeAction swipeRightAction, ShouldRefresh shouldRefresh) {
        LOG_TAG = log_tag;
        this.activity = activity;
        this.swrlListGenerator = swrlListGenerator;
        this.swipeLeftAction = swipeLeftAction;
        this.swipeRightAction = swipeRightAction;
        this.shouldRefresh = shouldRefresh;
        this.context = this.activity.getApplicationContext();
        this.collectionManager = collectionManager;
        swrls = new ArrayList<>();
        this.navListAdapter = navListAdapter;
        this.viewType = viewType;
    }

    public static SwrlListRecyclerAdapterFactory publicDiscover(ListActivity activity, CollectionManager collectionManager, DrawerListAdapter navListAdapter) {
        return new SwrlListRecyclerAdapterFactory("PUBLIC_DISCOVER_ADAPTER", activity, collectionManager, navListAdapter, publicSwrls(collectionManager), ADD_DISCOVER, discoverLeftAction, discoverRightAction, discoverRefreshCheck);
    }

    public static SwrlListRecyclerAdapterFactory weightedDiscover(ListActivity activity, CollectionManager collectionManager, DrawerListAdapter navListAdapter, SwrlPreferences preferences) {
        return new SwrlListRecyclerAdapterFactory("WEIGHTED_DISCOVER_ADAPTER", activity, collectionManager, navListAdapter, weightedSwrls(collectionManager, preferences), ADD_DISCOVER, discoverLeftAction, discoverRightAction, discoverRefreshCheck);
    }

    public static SwrlListRecyclerAdapterFactory inboxDiscover(ListActivity activity, CollectionManager collectionManager, DrawerListAdapter navListAdapter, SwrlPreferences preferences) {
        return new SwrlListRecyclerAdapterFactory("INBOX_DISCOVER_ADAPTER", activity, collectionManager, navListAdapter, inboxSwrls(collectionManager, preferences), ADD_DISCOVER, discoverLeftAction, discoverRightAction, discoverRefreshCheck);
    }

    public static SwrlListRecyclerAdapterFactory activeList(final ListActivity activity, final CollectionManager collectionManager, DrawerListAdapter navListAdapter) {
        return new SwrlListRecyclerAdapterFactory("ACTIVE_LIST_ADAPTER", activity, collectionManager, navListAdapter,
                new SwrlListGenerator() {
                    @Override
                    public List<Swrl> get() {
                        CollectionManager mCollectionManager = new SQLiteCollectionManager(activity);
                        return mCollectionManager.getActive();
                    }
                },
                VIEW,
                new SwipeAction() {
                    @Override
                    public void execute(final CollectionManager collectionManager, List<Swrl> swrls, List<Swrl> cachedSwrls, SwrlListRecyclerAdapterFactory adapter, ListActivity activity, DrawerListAdapter navListAdapter, RecyclerView.ViewHolder viewHolder, int position) {
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
                                activity, adapter, navListAdapter, viewHolder.itemView);

                    }
                },
                recommendSwipeAction,
                new ShouldRefresh() {
                    @Override
                    public boolean check(CollectionManager collectionManager, List<Swrl> cachedSwrls) {
                        return cachedSwrls.size() != collectionManager.countActive();
                    }
                });
    }

    public static SwrlListRecyclerAdapterFactory doneList(final ListActivity activity, final CollectionManager collectionManager, DrawerListAdapter navListAdapter) {
        return new SwrlListRecyclerAdapterFactory("DONE_LIST_ADAPTER", activity, collectionManager, navListAdapter,
                new SwrlListGenerator() {
                    @Override
                    public List<Swrl> get() {
                        CollectionManager mCollectionManager = new SQLiteCollectionManager(activity);
                        return mCollectionManager.getDone();
                    }
                },
                DONE,
                new SwipeAction() {
                    @Override
                    public void execute(final CollectionManager collectionManager, List<Swrl> swrls, List<Swrl> cachedSwrls, SwrlListRecyclerAdapterFactory adapter, ListActivity activity, DrawerListAdapter navListAdapter, RecyclerView.ViewHolder viewHolder, int position) {
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
                                activity, adapter, navListAdapter, viewHolder.itemView);

                    }
                },
                recommendSwipeAction,
                new ShouldRefresh() {
                    @Override
                    public boolean check(CollectionManager collectionManager, List<Swrl> cachedSwrls) {
                        return cachedSwrls.size() != collectionManager.countDone();
                    }
                });
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
        swrlRow.setRowClickToOpenViewByType(pagePosition, swrlsToPage, context, viewType);
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
        cancelExistingGetters();
        if (cachedSwrls == null || updateFromSource || shouldRefresh.check(collectionManager, cachedSwrls)) {
            backgroundGetter = new AsyncTask<Void, Void, List<Swrl>>() {
                @Override
                protected void onPreExecute() {
                    setSpinner(true);
                }

                @Override
                protected List<Swrl> doInBackground(Void... voids) {
                    return swrlListGenerator.get();
                }

                @Override
                protected void onPostExecute(List<Swrl> discoveredSwrls) {
                    cachedSwrls = discoveredSwrls;
                    updateAdapter(type, textFilter);
                }

                @Override
                protected void onCancelled() {
                    if (backgroundGetter == null || backgroundGetter.isCancelled())
                        setSpinner(false);
                }
            };
            backgroundGetter.execute();
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

    public void cancelExistingGetters() {
        if (backgroundGetter != null) {
            backgroundGetter.cancel(true);
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

    private void setSpinner(boolean spinning) {
        activity.setBackgroundDimming(spinning);
        activity.showSpinner(spinning);
    }

    @Override
    public void swipeLeft(RecyclerView.ViewHolder viewHolder, int position) {
        swipeLeftAction.execute(collectionManager, swrls, cachedSwrls, this, activity, navListAdapter, viewHolder, position);
    }

    @Override
    public void swipeRight(RecyclerView.ViewHolder viewHolder, int position) {
        swipeRightAction.execute(collectionManager, swrls, cachedSwrls, this, activity, navListAdapter, viewHolder, position);
    }
}
