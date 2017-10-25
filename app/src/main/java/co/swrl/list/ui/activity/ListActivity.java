package co.swrl.list.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.swrl.list.R;
import co.swrl.list.SwrlPreferences;
import co.swrl.list.collection.CollectionManager;
import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;
import co.swrl.list.item.search.Search;
import co.swrl.list.ui.SwrlDialogs;
import co.swrl.list.ui.list.ActiveSwrlListRecyclerAdapter;
import co.swrl.list.ui.list.DiscoverSwrlListRecyclerAdapter;
import co.swrl.list.ui.list.DoneSwrlListRecyclerAdapter;
import co.swrl.list.ui.list.SwrlListRecyclerAdapter;
import co.swrl.list.ui.list.SwrlListViewFactory;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ListActivity extends AppCompatActivity {

    private final int doneColor = R.color.add;
    private final int deleteColor = R.color.delete;
    private final int discoverColor = R.color.add;
    private int swipeColor = doneColor;

    private final int doneIcon = R.drawable.ic_done_black_24dp;
    private final int deleteIcon = R.drawable.ic_delete_black_24dp;
    private final int discoverIcon = R.drawable.ic_add_black_24dp;
    private int swipeIcon = doneIcon;

    private final int swrl_list_title = R.string.app_title;
    private final int done_title = R.string.done_title;
    private final int discover_title = R.string.discover_title;
    private int title = swrl_list_title;

    private SwrlListRecyclerAdapter activeSwrlListAdapter;
    private SwrlListRecyclerAdapter doneSwrlListAdapter;
    private SwrlListRecyclerAdapter discoverSwrlListAdapter;
    private SwrlListRecyclerAdapter swrlListAdapter;

    private boolean showingMainButtons = true;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Type typeFilter;
    private LinearLayout nav_drawer;
    private SQLiteCollectionManager collectionManager;
    private DrawerListAdapter navListAdapter = new DrawerListAdapter(this, Type.values());
    private static final String LIST_ACTIVITY = "LIST_ACTIVITY";
    private SwipeSimpleCallback swipeCallback;
    private final SwipeItemDecoration swipeItemDecoration = new SwipeItemDecoration();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showWhatsNewDialogIfNewVersion(new SwrlPreferences(this), new SwrlDialogs(this));
        collectionManager = new SQLiteCollectionManager(this);
        setUpViewElements(collectionManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
        navListAdapter.notifyDataSetChanged();
        setNoSwrlsText();
        FloatingActionsMenu addSwrlMenu = (FloatingActionsMenu) findViewById(R.id.addItemFAB);
        addSwrlMenu.collapseImmediately();
    }

    @Override
    public void onBackPressed() {
        FloatingActionsMenu addSwrlMenu = (FloatingActionsMenu) findViewById(R.id.addItemFAB);
        if (addSwrlMenu.isExpanded()) {
            addSwrlMenu.collapse();
        } else {
            super.onBackPressed();
        }
        setNoSwrlsText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
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
        if (id == R.id.action_refresh_all) {
            new AsyncTask<ArrayList<?>, Void, Void>() {
                @Override
                protected Void doInBackground(ArrayList<?>... arrayLists) {
                    ArrayList<?> swrls = arrayLists[0];
                    for (Object swrl : swrls) {
                        Swrl mSwrl = (Swrl) swrl;
                        if (mSwrl.getDetails() != null && mSwrl.getDetails().getId() != null && !mSwrl.getDetails().getId().isEmpty()) {
                            Search search = mSwrl.getType().getSearch();
                            Details details = search.byID(mSwrl.getDetails().getId());
                            if (details != null) {
                                collectionManager.saveDetails(mSwrl, details);
                            }
                        }
                    }
                    return null;
                }
            }.execute((ArrayList<?>) collectionManager.getAll());
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshList() {
        if (noTypeFilterSet()) {
            swrlListAdapter.refreshAll();

        } else {
            swrlListAdapter.refreshAllWithFilter(typeFilter);
        }
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
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(title);
        }
        activeSwrlListAdapter = new ActiveSwrlListRecyclerAdapter(this, collectionManager, navListAdapter);
        doneSwrlListAdapter = new DoneSwrlListRecyclerAdapter(this, collectionManager, navListAdapter);
        discoverSwrlListAdapter = new DiscoverSwrlListRecyclerAdapter(this, collectionManager, navListAdapter);
        swrlListAdapter = activeSwrlListAdapter;
        setUpList();
        setUpAddSwrlButtons();
        setUpNavigationDrawer();
        setUpBottomNavigation();
    }

    private void setUpBottomNavigation() {
        BottomNavigationView navigationMenuView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationMenuView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.active_swrls) {
                    Log.d(LIST_ACTIVITY, "clicked active swrls");
                    swrlListAdapter = activeSwrlListAdapter;
                    swipeColor = doneColor;
                    swipeIcon = doneIcon;
                    setUpList();
                    title = swrl_list_title;
                    resetTitle();
                    navListAdapter.notifyDataSetChanged();
                }
                if (id == R.id.done_swrls) {
                    Log.d(LIST_ACTIVITY, "clicked done swrls");
                    swrlListAdapter = doneSwrlListAdapter;
                    swipeColor = deleteColor;
                    swipeIcon = deleteIcon;
                    setUpList();
                    title = done_title;
                    resetTitle();
                    navListAdapter.notifyDataSetChanged();
                }
                if (id == R.id.discover) {
                    Log.d(LIST_ACTIVITY, "clicked discover");
                    swrlListAdapter = discoverSwrlListAdapter;
                    swipeColor = discoverColor;
                    swipeIcon = discoverIcon;
                    setUpList();
                    title = discover_title;
                    resetTitle();
                    navListAdapter.notifyDataSetChanged();
                }
                return true;
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
                refreshList();
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
        refreshList();
        if (swipeCallback != null) {
            swipeCallback.forceReDraw();
        }
        swipeItemDecoration.forceReDraw();
        setUpItemTouchHelper(list);
        setUpAnimationDecoratorHelper(list);
        setNoSwrlsText();
    }

    public void setNoSwrlsText() {
        String content = "No "
                + (noTypeFilterSet() ? "Swrls" : typeFilter.getFriendlyNamePlural())
                + " yet!\n\nAdd some by clicking the button below.";
        TextView noSwrlsText = (TextView) findViewById(R.id.noSwrlsText);
        noSwrlsText.setText(content);
        if (swrlListAdapter.getSwrlCount() > 0) {
            noSwrlsText.setVisibility(GONE);
        } else {
            noSwrlsText.setVisibility(VISIBLE);
        }
    }

    private void setUpItemTouchHelper(final RecyclerView recyclerView) {
        if (swipeCallback == null) {
            swipeCallback = new SwipeSimpleCallback(recyclerView);
        }
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(swipeCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to their new positions
     * after an item is removed.
     */
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
        ProgressBar spinner = (ProgressBar) findViewById(R.id.progressBar);
        if (spinner != null) {
            if (show) {
                spinner.setVisibility(VISIBLE);
            } else {
                spinner.setVisibility(GONE);
            }
        }
    }

    private void setUpAddSwrlButtons() {
        setUpDimmerBackground();

        @SuppressLint("UseSparseArrays")
        final HashMap<Integer, Type> otherButtons = new HashMap<>();
        @SuppressLint("UseSparseArrays")
        final HashMap<Integer, Type> mainButtons = new HashMap<>();

        mainButtons.put(R.id.add_film, Type.FILM);
        mainButtons.put(R.id.add_album, Type.ALBUM);
        mainButtons.put(R.id.add_board_game, Type.BOARD_GAME);
        mainButtons.put(R.id.add_tv, Type.TV);
        mainButtons.put(R.id.add_book, Type.BOOK);

        otherButtons.put(R.id.add_podcast, Type.PODCAST);
        otherButtons.put(R.id.add_phone_app, Type.APP);
        otherButtons.put(R.id.add_video_game, Type.VIDEO_GAME);

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

    public class DrawerListAdapter extends BaseAdapter {

        private final Context mContext;
        private final Type[] mNavItems;

        DrawerListAdapter(Context context, Type[] navItems) {
            this.mContext = context;
            this.mNavItems = navItems;
        }

        @Override
        public int getCount() {
            return mNavItems.length;
        }

        @Override
        public Object getItem(int i) {
            return mNavItems[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.drawer_item, null);
            } else {
                view = convertView;
            }

            ImageView iconView = (ImageView) view.findViewById(R.id.icon);
            TextView titleView = (TextView) view.findViewById(R.id.title);

            Type navItem = mNavItems[position];
            if ((typeFilter == null && navItem == Type.UNKNOWN) || navItem == typeFilter) {
                //noinspection deprecation
                view.setBackgroundColor(getResources().getColor(R.color.rowHighlight));
            } else {
                view.setBackgroundColor(Color.WHITE);
            }
            ImageView border = (ImageView) view.findViewById(R.id.nav_left_border);
            //noinspection deprecation
            border.setBackgroundColor(getApplicationContext().getResources().getColor(navItem.getColor()));
            iconView.setImageResource(navItem.getIcon());
            String filterTitle = navItem.getFriendlyNamePlural()
                    + " ("
                    + (navItem == Type.UNKNOWN ? swrlListAdapter.getSwrlCount() : swrlListAdapter.getSwrlCount(navItem))
                    + ")";
            titleView.setText(filterTitle);
            return view;
        }
    }

    private class SwipeSimpleCallback extends ItemTouchHelper.SimpleCallback {

        private final RecyclerView recyclerView;
        // we want to cache these and not allocate anything repeatedly in the onChildDraw method
        Drawable background;
        Drawable xMark;
        int xMarkMargin;
        boolean initiated;

        SwipeSimpleCallback(RecyclerView recyclerView) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            this.recyclerView = recyclerView;
        }

        private void init() {
            Log.d(LIST_ACTIVITY, "Initiating Item Touch Helper");
            //noinspection deprecation
            background = new ColorDrawable(getResources().getColor(swipeColor));
            xMark = ContextCompat.getDrawable(ListActivity.this, swipeIcon);
            xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            xMarkMargin = (int) ListActivity.this.getResources().getDimension(R.dimen.ic_clear_margin);
            initiated = true;
        }

        void forceReDraw() {
            initiated = false;
        }

        // not important, we don't want drag & drop
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return super.getSwipeDirs(recyclerView, viewHolder);
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int swipedPosition = viewHolder.getAdapterPosition();
            SwrlListRecyclerAdapter adapter = (SwrlListRecyclerAdapter) recyclerView.getAdapter();
            adapter.swipeAction(viewHolder, swipedPosition);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;

            // not sure why, but this method get's called for viewholder that are already swiped away
            if (viewHolder.getAdapterPosition() == -1) {
                // not interested in those
                return;
            }

            if (!initiated) {
                init();
            }

            // draw green background
            if (dX > 0) { //swipe right
                background.setBounds(itemView.getLeft() - (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            } else {
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            }
            background.draw(c);

            // draw mark
            int itemHeight = itemView.getBottom() - itemView.getTop();
            int intrinsicWidth = xMark.getIntrinsicWidth();
            int intrinsicHeight = xMark.getIntrinsicWidth();

            int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int xMarkBottom = xMarkTop + intrinsicHeight;

            int xMarkLeft;
            int xMarkRight;

            if (dX > 0) { //swipe Right
                xMarkLeft = itemView.getLeft() + xMarkMargin;
                xMarkRight = itemView.getLeft() + xMarkMargin + intrinsicWidth;
            } else {
                xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                xMarkRight = itemView.getRight() - xMarkMargin;
            }
            xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
            xMark.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    }

    private class SwipeItemDecoration extends RecyclerView.ItemDecoration {

        // we want to cache this and not allocate anything repeatedly in the onDraw method
        Drawable background;
        boolean initiated;

        private void init() {
            Log.d(LIST_ACTIVITY, "Initiating Animation decoration Helper");
            //noinspection deprecation
            background = new ColorDrawable(getResources().getColor(swipeColor));
            initiated = true;
        }

        void forceReDraw() {
            initiated = false;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

            if (!initiated) {
                init();
            }

            // only if animation is in progress
            if (parent.getItemAnimator().isRunning()) {

                // some items might be animating down and some items might be animating up to close the gap left by the removed item
                // this is not exclusive, both movement can be happening at the same time
                // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                // then swipeAction one from the middle

                // find first child with translationY > 0
                // and last one with translationY < 0
                // we're after a rect that is not covered in recycler-view views at this point in time
                View lastViewComingDown = null;
                View firstViewComingUp = null;

                // this is fixed
                int left = 0;
                int right = parent.getWidth();

                // this we need to find out
                int top = 0;
                int bottom = 0;

                // find relevant translating views
                int childCount = parent.getLayoutManager().getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = parent.getLayoutManager().getChildAt(i);
                    if (child.getTranslationY() < 0) {
                        // view is coming down
                        lastViewComingDown = child;
                    } else if (child.getTranslationY() > 0) {
                        // view is coming up
                        if (firstViewComingUp == null) {
                            firstViewComingUp = child;
                        }
                    }
                }

                if (lastViewComingDown != null && firstViewComingUp != null) {
                    // views are coming down AND going up to fill the void
                    top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                    bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                } else if (lastViewComingDown != null) {
                    // views are going down to fill the void
                    top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                    bottom = lastViewComingDown.getBottom();
                } else if (firstViewComingUp != null) {
                    // views are coming up to fill the void
                    top = firstViewComingUp.getTop();
                    bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                }

                background.setBounds(left, top, right, bottom);
                background.draw(c);

            }
            super.onDraw(c, parent, state);
        }

    }
}
