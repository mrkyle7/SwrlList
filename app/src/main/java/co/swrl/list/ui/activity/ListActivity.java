package co.swrl.list.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import co.swrl.list.R;
import co.swrl.list.collection.CollectionManager;
import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Type;
import co.swrl.list.tasks.RefreshAllDetailsTask;
import co.swrl.list.tasks.SyncAllSwrlsTask;
import co.swrl.list.ui.list.menus.DrawerListAdapter;
import co.swrl.list.ui.list.swrllists.ActiveSwrlListRecyclerAdapter;
import co.swrl.list.ui.list.swrllists.DiscoverSwrlListRecyclerAdapter;
import co.swrl.list.ui.list.swrllists.DoneSwrlListRecyclerAdapter;
import co.swrl.list.ui.list.swrllists.SwrlListRecyclerAdapter;
import co.swrl.list.ui.list.utils.SwipeItemDecoration;
import co.swrl.list.ui.list.utils.SwipeSimpleCallback;
import co.swrl.list.ui.list.utils.SwrlListViewFactory;
import co.swrl.list.utils.SwrlDialogs;
import co.swrl.list.utils.SwrlPreferences;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static co.swrl.list.ui.list.swrllists.DiscoverSwrlListRecyclerAdapter.inboxDiscover;
import static co.swrl.list.ui.list.swrllists.DiscoverSwrlListRecyclerAdapter.publicDiscover;
import static co.swrl.list.ui.list.swrllists.DiscoverSwrlListRecyclerAdapter.weightedDiscover;

public class ListActivity extends AppCompatActivity {

    public final SwrlPreferences preferences = new SwrlPreferences(this);

    private final int green = R.color.add;
    private final int red = R.color.delete;
    public AtomicInteger swipeLeftColor = new AtomicInteger(green);
    public AtomicInteger swipeRightColor = new AtomicInteger(green);
    public AtomicInteger animationColor = new AtomicInteger(green);

    private final int doneIcon = R.drawable.ic_done_black_24dp;
    private final int deleteIcon = R.drawable.ic_delete_black_24dp;
    private final int addIcon = R.drawable.ic_add_black_24dp;
    private final int shareIcon = R.drawable.ic_share_black_24dp;
    private AtomicInteger swipeLeftIcon = new AtomicInteger(doneIcon);
    private AtomicInteger swipeRightIcon = new AtomicInteger(shareIcon);

    private final int swrl_list_title = R.string.app_title;
    private final int done_title = R.string.done_title;
    private final int discover_title = R.string.discover_title;
    private final int inbox_title = R.string.inbox_title;
    private int title = swrl_list_title;

    private final static int ACTIVE = 1;
    private final static int DONE = 2;
    private final static int DISCOVER = 3;
    private final static int DISCOVER_WEIGHTED = 4;
    private final static int INBOX = 5;
    private int currentListType = ACTIVE;

    private final int listNoSwrlsText = R.string.listNoSwrlsText;
    private final int doneNoSwrlsText = R.string.doneNoSwrlsText;
    private final int discoverNoSwrlsText = R.string.discoverNoSwrlsText;
    private final int inboxNoSwrlsText = R.string.inboxNoSwrlsText;
    private int noSwrlsText = listNoSwrlsText;


    private SwrlListRecyclerAdapter activeSwrlListAdapter;
    private SwrlListRecyclerAdapter doneSwrlListAdapter;
    private SwrlListRecyclerAdapter weightedDiscoverSwrlListAdapter;
    private SwrlListRecyclerAdapter inboxDiscoverSwrlListAdapter;
    private SwrlListRecyclerAdapter discoverSwrlListAdapter;
    public SwrlListRecyclerAdapter swrlListAdapter;


    private boolean showingMainButtons = true;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    public Type typeFilter = Type.UNKNOWN;
    private String textFilter = "";
    private LinearLayout nav_drawer;
    private final DrawerListAdapter navListAdapter = new DrawerListAdapter(this, this, Type.values());
    private static final String LOG_TAG = "ListActivity";
    private SwipeSimpleCallback swipeCallback;
    private final SwipeItemDecoration swipeItemDecoration = new SwipeItemDecoration(this, animationColor);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showWhatsNewDialogIfNewVersion(preferences, new SwrlDialogs(this));
        SQLiteCollectionManager collectionManager = new SQLiteCollectionManager(this);
        setUpViewElements(collectionManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        setUpBottomNavigation();
        collapseAddSwrlMenu();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        collapseAddSwrlMenu();
        setNoSwrlsText();
    }

    private void collapseAddSwrlMenu() {
        FloatingActionsMenu addSwrlMenu = (FloatingActionsMenu) findViewById(R.id.addItemFAB);
        if (addSwrlMenu.isExpanded()) {
            addSwrlMenu.collapse();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (preferences.loggedIn()) {
            getMenuInflater().inflate(R.menu.menu_list_logged_in, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_list_logged_out, menu);
        }
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                textFilter = query;
                refreshList(false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                textFilter = newText;
                refreshList(false);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();
        // Handle your other action bar items...
        if (id == R.id.action_refresh) {
            refreshAction();
        }
        if (id == R.id.action_refresh_all_details) {
            new RefreshAllDetailsTask(this, this).execute();
        }
        if (id == R.id.action_sync_swrls) {
            new SyncAllSwrlsTask(this, this).execute();
        }
        if (id == R.id.actions_show_whats_new) {
            new SwrlDialogs(this).buildAndShowWhatsNewDialog();
        }
        if (id == R.id.action_go_to_login) {
            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginActivity, null);
        }
        if (id == R.id.action_logout) {
            AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this);
            confirmDialog.setTitle("Do you really want to logout?");
            confirmDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    preferences.saveUserID(0);
                    preferences.saveAuthToken(null);
                    onResume();
                }
            });
            confirmDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            confirmDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }


    public void refreshList(boolean updateFromSource) {
        swrlListAdapter.refreshList(typeFilter, textFilter, updateFromSource);
        navListAdapter.notifyDataSetChanged();
        setNoSwrlsText();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    public static void showWhatsNewDialogIfNewVersion(SwrlPreferences preferences, SwrlDialogs dialogs) {
        if (preferences.isPackageNewVersion()) {
            dialogs.buildAndShowWhatsNewDialog();
            preferences.savePackageVersionAsCurrentVersion();
        }
    }

    private void setUpViewElements(CollectionManager collectionManager) {
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(title);
        activeSwrlListAdapter = new ActiveSwrlListRecyclerAdapter(this, collectionManager, navListAdapter);
        doneSwrlListAdapter = new DoneSwrlListRecyclerAdapter(this, collectionManager, navListAdapter);
        discoverSwrlListAdapter = publicDiscover(this, collectionManager, navListAdapter);
        weightedDiscoverSwrlListAdapter = weightedDiscover(this, collectionManager, navListAdapter, preferences);
        inboxDiscoverSwrlListAdapter = inboxDiscover(this, collectionManager, navListAdapter, preferences);
        swrlListAdapter = activeSwrlListAdapter;
        setUpList();
        setUpAddSwrlButtons();
        setUpNavigationDrawer();
        setUpBottomNavigation();
        setUpRefreshListener();
    }

    private void setUpRefreshListener() {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAction();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.video),
                getResources().getColor(R.color.website),
                getResources().getColor(R.color.film),
                getResources().getColor(R.color.book),
                getResources().getColor(R.color.album));
    }

    public void enableDisableSwipeRefresh(boolean enable) {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setEnabled(enable);
        }
    }

    private void refreshAction() {
        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
        collapseAddSwrlMenu();
        refreshList(true);
    }

    private void setUpBottomNavigation() {
        BottomNavigationView navigationMenuView = (BottomNavigationView) findViewById(R.id.navigation);
        int previousSelected = navigationMenuView.getSelectedItemId();
        navigationMenuView.getMenu().clear();
        if (preferences.loggedIn()) {
            navigationMenuView.inflateMenu(R.menu.navigation_logged_in);
            if (previousSelected == R.id.discover) {
                navigationMenuView.setSelectedItemId(R.id.discover_weighted);
            } else {
                navigationMenuView.setSelectedItemId(previousSelected);
            }
        } else {
            navigationMenuView.inflateMenu(R.menu.navigation_logged_out);
            if (previousSelected == R.id.discover_weighted) {
                navigationMenuView.setSelectedItemId(R.id.discover);
            } else if (previousSelected == R.id.inbox) {
                navigationMenuView.setSelectedItemId(R.id.active_swrls);
            } else {
                navigationMenuView.setSelectedItemId(previousSelected);
            }
        }
        navigationMenuView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.active_swrls) {
                    if (swrlListAdapter instanceof DiscoverSwrlListRecyclerAdapter) {
                        ((DiscoverSwrlListRecyclerAdapter) swrlListAdapter).cancelExistingSearches();
                    }
                    switchViewToCurrentTab(ACTIVE,
                            activeSwrlListAdapter,
                            green,
                            doneIcon,
                            green,
                            shareIcon,
                            swrl_list_title,
                            listNoSwrlsText, "clicked active swrls");
                }
                if (id == R.id.done_swrls) {
                    if (swrlListAdapter instanceof DiscoverSwrlListRecyclerAdapter) {
                        ((DiscoverSwrlListRecyclerAdapter) swrlListAdapter).cancelExistingSearches();
                    }
                    switchViewToCurrentTab(DONE,
                            doneSwrlListAdapter,
                            red,
                            deleteIcon,
                            green,
                            shareIcon,
                            done_title,
                            doneNoSwrlsText, "clicked done swrls");
                }
                if (id == R.id.discover) {
                    switchViewToCurrentTab(DISCOVER,
                            discoverSwrlListAdapter,
                            red,
                            deleteIcon,
                            green,
                            addIcon,
                            discover_title,
                            discoverNoSwrlsText, "clicked discover");
                }
                if (id == R.id.discover_weighted) {
                    switchViewToCurrentTab(DISCOVER_WEIGHTED,
                            weightedDiscoverSwrlListAdapter,
                            red,
                            deleteIcon,
                            green,
                            addIcon,
                            discover_title,
                            discoverNoSwrlsText,
                            "clicked discover weighted");
                }
                if (id == R.id.inbox) {
                    switchViewToCurrentTab(INBOX,
                            inboxDiscoverSwrlListAdapter,
                            red,
                            deleteIcon,
                            green,
                            addIcon,
                            inbox_title,
                            inboxNoSwrlsText, "clicked inbox");
                }
                return true;
            }

            private void switchViewToCurrentTab(int listType, SwrlListRecyclerAdapter swrlListAdapter, int swipeLeftColor, int swipeLeftIcon, int swipeRightColor, int swipeRightIcon, int title, int noSwrlsText, String logMessage) {
                if (listType != currentListType) {
                    currentListType = listType;
                    Log.d(LOG_TAG, logMessage);
                    ListActivity.this.swrlListAdapter = swrlListAdapter;
                    ListActivity.this.swipeLeftColor.set(swipeLeftColor);
                    ListActivity.this.swipeLeftIcon.set(swipeLeftIcon);
                    ListActivity.this.swipeRightColor.set(swipeRightColor);
                    ListActivity.this.swipeRightIcon.set(swipeRightIcon);
                    ListActivity.this.title = title;
                    ListActivity.this.noSwrlsText = noSwrlsText;
                    setUpList();
                    resetTitle();
                    navListAdapter.notifyDataSetChanged();
                    setNoSwrlsText();
                } else {
                    Log.i(LOG_TAG, "Already showing current List view. Just needs Refreshing");
                    refreshList(false);
                }
            }
        });
    }

    private void setUpNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        nav_drawer = (LinearLayout) findViewById(R.id.nav_drawer);
        final ListView drawerList = (ListView) findViewById(R.id.swrl_filter_list);
        drawerList.setAdapter(navListAdapter);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                typeFilter = (Type) drawerList.getAdapter().getItem(position);
                mDrawerLayout.closeDrawer(nav_drawer);
                refreshList(false);
                navListAdapter.notifyDataSetChanged();
                setNoSwrlsText();
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_closed) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                resetTitle();
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeButtonEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void resetTitle() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            if (noTypeFilterSet()) {
                supportActionBar.setTitle(getApplicationContext().getResources().getString(title));
            } else {
                supportActionBar.setTitle(getApplicationContext().getResources().getString(title) + " - " + typeFilter.getFriendlyNamePlural());
            }
        }
    }

    private boolean noTypeFilterSet() {
        return typeFilter == null || typeFilter == Type.UNKNOWN;
    }

    private void setUpList() {
        RecyclerView list = SwrlListViewFactory.setUpListView(this, (RecyclerView) findViewById(R.id.listView), (RecyclerView.Adapter) swrlListAdapter);
        refreshList(false);
        if (swipeCallback != null) {
            swipeCallback.forceReDraw();
        }
        swipeItemDecoration.forceReDraw();
        setUpItemTouchHelper(list);
        setUpAnimationDecoratorHelper(list);
        setNoSwrlsText();
    }

    public void setNoSwrlsText() {
        TextView noSwrlsTextView = (TextView) findViewById(R.id.noSwrlsText);
        noSwrlsTextView.setText(getApplicationContext().getResources().getString(noSwrlsText));
        if (swrlListAdapter.getSwrlCount() > 0) {
            noSwrlsTextView.setVisibility(GONE);
        } else {
            noSwrlsTextView.setVisibility(VISIBLE);
        }
    }

    private void setUpItemTouchHelper(final RecyclerView recyclerView) {
        if (swipeCallback == null) {
            swipeCallback = new SwipeSimpleCallback(this, recyclerView, swipeItemDecoration, swipeLeftColor, swipeLeftIcon, swipeRightColor, swipeRightIcon);
        }
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(swipeCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setUpAnimationDecoratorHelper(RecyclerView recyclerView) {
        recyclerView.addItemDecoration(swipeItemDecoration);
    }

    public void setBackgroundDimming(boolean dimmed) {
        final float targetAlpha = dimmed ? 1f : 0;
        final int endVisibility = dimmed ? View.VISIBLE : View.GONE;
        final View mDimmerView = findViewById(R.id.dimmer_view);
        mDimmerView.setVisibility(View.VISIBLE);
        mDimmerView.animate()
                .alpha(targetAlpha)
                .setDuration(300)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mDimmerView.setVisibility(endVisibility);
                    }
                })
                .start();
    }

    public void showSpinner(boolean show) {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setRefreshing(show);
    }

    private void setUpAddSwrlButtons() {
        setUpDimmerBackground();

        @SuppressLint("UseSparseArrays")
        final HashMap<Integer, Type> otherButtons = new HashMap<>();
        @SuppressLint("UseSparseArrays")
        final HashMap<Integer, Type> mainButtons = new HashMap<>();

        mainButtons.put(R.id.add_film, Type.FILM);
        mainButtons.put(R.id.add_board_game, Type.BOARD_GAME);
        mainButtons.put(R.id.add_tv, Type.TV);
        mainButtons.put(R.id.add_book, Type.BOOK);

        otherButtons.put(R.id.add_podcast, Type.PODCAST);
        otherButtons.put(R.id.add_phone_app, Type.APP);
        otherButtons.put(R.id.add_video_game, Type.VIDEO_GAME);
        otherButtons.put(R.id.add_album, Type.ALBUM);


        enableButtons(mainButtons);
        disableButtons(otherButtons);

        FloatingActionButton moreButton = (FloatingActionButton) findViewById(R.id.show_others);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showingMainButtons) {
                    disableButtons(mainButtons);
                    enableButtons(otherButtons);
                    showingMainButtons = false;
                } else {
                    disableButtons(otherButtons);
                    enableButtons(mainButtons);
                    showingMainButtons = true;
                }
            }
        });

    }

    private void setUpDimmerBackground() {
        FloatingActionsMenu addSwrlMenu = (FloatingActionsMenu) findViewById(R.id.addItemFAB);

        addSwrlMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                setBackgroundDimming(true);
            }

            @Override
            public void onMenuCollapsed() {
                setBackgroundDimming(false);
            }
        });

        View mDimmerView = findViewById(R.id.dimmer_view);
        mDimmerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(GONE);
                FloatingActionsMenu addSwrlMenu = (FloatingActionsMenu) findViewById(R.id.addItemFAB);
                addSwrlMenu.collapse();
                if (swrlListAdapter instanceof DiscoverSwrlListRecyclerAdapter) {
                    ((DiscoverSwrlListRecyclerAdapter) swrlListAdapter).cancelExistingSearches();
                }
            }
        });
    }

    private void enableButtons(HashMap<Integer, Type> buttons) {
        for (final Map.Entry<Integer, Type> button : buttons.entrySet()) {
            FloatingActionButton actionButton = (FloatingActionButton) findViewById(button.getKey());
            actionButton.setVisibility(VISIBLE);
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addSwrlActivity = new Intent(getApplicationContext(), AddSwrlActivity.class);
                    addSwrlActivity.putExtra(AddSwrlActivity.EXTRAS_TYPE, button.getValue());
                    startActivity(addSwrlActivity, null);
                }
            });
        }
    }

    private void disableButtons(HashMap<Integer, Type> buttons) {
        for (final Map.Entry<Integer, Type> button : buttons.entrySet()) {
            FloatingActionButton actionButton = (FloatingActionButton) findViewById(button.getKey());
            actionButton.setVisibility(GONE);
        }
    }
}
