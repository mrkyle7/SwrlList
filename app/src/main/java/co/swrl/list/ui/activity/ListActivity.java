package co.swrl.list.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import co.swrl.list.ui.list.SwrlListRecyclerAdapter;
import co.swrl.list.ui.list.SwrlListViewFactory;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ListActivity extends AppCompatActivity {

    private SwrlListRecyclerAdapter swrlListAdapter;
    private boolean showingMainButtons = true;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Type typeFilter;
    private LinearLayout nav_drawer;
    private SQLiteCollectionManager collectionManager;
    private DrawerListAdapter navListAdapter = new DrawerListAdapter(this, Type.values());

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
        if (typeFilter == null || typeFilter == Type.UNKNOWN) {
            swrlListAdapter.refreshAll();
        } else {
            swrlListAdapter.refreshAllWithFilter(typeFilter);
        }
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
        if (id == R.id.action_refresh_all){
            new AsyncTask<ArrayList<?>,Void, Void>() {
                @Override
                protected Void doInBackground(ArrayList<?>... arrayLists) {
                    ArrayList<?> swrls = arrayLists[0];
                    for(Object swrl : swrls){
                        Swrl mSwrl = (Swrl) swrl;
                        if (mSwrl.getDetails() != null && mSwrl.getDetails().getId() != null && !mSwrl.getDetails().getId().isEmpty())
                        {
                            Search search = mSwrl.getType().getSearch();
                            Details details = search.byID(mSwrl.getDetails().getId());
                            swrlListAdapter.updateSwrl(mSwrl);
                            if (details != null){
                                collectionManager.saveDetails(mSwrl, details);
                                swrlListAdapter.updateSwrl(mSwrl);
                            }
                        }
                    }
                    return null;
                }
            }.execute((ArrayList<?>) swrlListAdapter.getSwrls());
        }

        return super.onOptionsItemSelected(item);
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
        getSupportActionBar().setTitle(R.string.app_title);
        swrlListAdapter = new SwrlListRecyclerAdapter(this, collectionManager, navListAdapter);
        setUpList();
        setUpAddSwrlButtons();
        setUpNavigationDrawer();
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
                if (typeFilter == null || typeFilter == Type.UNKNOWN) {
                    swrlListAdapter.refreshAll();
                } else {
                    swrlListAdapter.refreshAllWithFilter(typeFilter);
                }
                navListAdapter.notifyDataSetChanged();
                mDrawerLayout.closeDrawer(nav_drawer);
                setNoSwrlsText();
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_closed) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                ActionBar supportActionBar = getSupportActionBar();
                if (typeFilter == null || typeFilter == Type.UNKNOWN) {
                    supportActionBar.setTitle(getApplicationContext().getResources().getString(R.string.app_title));
                } else {
                    supportActionBar.setTitle(getApplicationContext().getResources().getString(R.string.app_title) + " - " + typeFilter.getFriendlyNamePlural());
                }
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpList() {
        RecyclerView list = SwrlListViewFactory.setUpListView(this, (RecyclerView) findViewById(R.id.listView), swrlListAdapter);
        setUpItemTouchHelper(list);
        setUpAnimationDecoratorHelper(list);
        setNoSwrlsText();
    }

    public void setNoSwrlsText() {
        String content = "No "
                + (typeFilter == null || typeFilter == Type.UNKNOWN ? "Swrls" : typeFilter.getFriendlyNamePlural())
                + " yet!\n\nAdd some by clicking the button below.";
        TextView noSwrlsText = (TextView) findViewById(R.id.noSwrlsText);
        noSwrlsText.setText(content);
        if (swrlListAdapter.getItemCount() > 0) {
            noSwrlsText.setVisibility(GONE);
        } else {
            noSwrlsText.setVisibility(VISIBLE);
        }
    }

    private void setUpItemTouchHelper(final RecyclerView recyclerView) {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(getResources().getColor(R.color.add));
                xMark = ContextCompat.getDrawable(ListActivity.this, R.drawable.ic_done_black_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) ListActivity.this.getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
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
                adapter.markAsDone(viewHolder, swipedPosition);
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

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to their new positions
     * after an item is removed.
     */
    private void setUpAnimationDecoratorHelper(RecyclerView recyclerView) {
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(getResources().getColor(R.color.add));
                initiated = true;
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
                    // then markAsDone one from the middle

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

        });
    }

    private void setUpAddSwrlButtons() {
        final HashMap<Integer, Type> otherButtons = new HashMap<>();
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

        public DrawerListAdapter(Context context, Type[] navItems) {
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
                view.setBackgroundColor(getResources().getColor(R.color.rowHighlight));
            } else {
                view.setBackgroundColor(Color.WHITE);
            }
            ImageView border = (ImageView) view.findViewById(R.id.nav_left_border);
            border.setBackgroundColor(getApplicationContext().getResources().getColor(navItem.getColor()));
            iconView.setImageResource(navItem.getIcon());
            String filterTitle = navItem.getFriendlyNamePlural()
                    + " ("
                    + (navItem == Type.UNKNOWN ? collectionManager.countActive() : collectionManager.countActiveByFilter(navItem))
                    + ")";
            titleView.setText(filterTitle);
            return view;
        }
    }
}
