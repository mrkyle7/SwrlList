package co.swrl.list.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import co.swrl.list.R;
import co.swrl.list.collection.CollectionManager;
import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.search.Search;

import static co.swrl.list.ui.activity.ViewActivity.ViewType.ADD;

public class ViewActivity extends AppCompatActivity {

    private static final String LOG_TAG = "VIEW_ACTIVITY";
    private ArrayList<?> swrls;
    private int firstSwrlIndex;
    private ViewType viewType;
    private CollectionManager db;
    private Swrl currentSwrl;


    public static final String EXTRAS_SWRLS = "swrls";
    public static final String EXTRAS_INDEX = "index";
    public static final String EXTRAS_TYPE = "type";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private int position;

    public enum ViewType {
        VIEW,
        DONE,
        ADD,
        ADD_DISCOVER
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        setSwrls();
        setFirstSwrlIndex();
        setViewType();
        setupView();

        db = new SQLiteCollectionManager(getApplicationContext());
    }

    private void setUpRefreshListener() {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
                if (currentSwrl.getDetails() != null && currentSwrl.getDetails().getId() != null
                        && !currentSwrl.getDetails().getId().isEmpty()) {
                    new GetSwrlDetails(swipeRefreshLayout).execute(currentSwrl.getDetails().getId());
                }
            }
        });
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.video),
                getResources().getColor(R.color.website),
                getResources().getColor(R.color.film),
                getResources().getColor(R.color.book),
                getResources().getColor(R.color.album));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        switch (viewType) {
            case VIEW:
                getMenuInflater().inflate(R.menu.menu_view, menu);
                break;
            case DONE:
                getMenuInflater().inflate(R.menu.menu_view_done, menu);
                break;
            case ADD:
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            Log.d(LOG_TAG, "Current Swrl = " + currentSwrl);
            if (currentSwrl.getDetails() != null && currentSwrl.getDetails().getId() != null
                    && !currentSwrl.getDetails().getId().isEmpty()) {
                new GetSwrlDetails((SwipeRefreshLayout) findViewById(R.id.swiperefresh)).execute(currentSwrl.getDetails().getId());
            }
            return true;
        } else if (id == R.id.action_markAsDone) {
            Log.d(LOG_TAG, "Current Swrl = " + currentSwrl);
            AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this);
            confirmDialog.setTitle("Mark the Swrl as 'done' and remove from list?");
            confirmDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.markAsDone((Swrl) swrls.get(position));
                    mSectionsPagerAdapter.deletePage(position);
                }
            });
            confirmDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            confirmDialog.show();
            return true;
        } else if (id == R.id.action_delete) {
            Log.d(LOG_TAG, "Current Swrl = " + currentSwrl);
            AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this);
            confirmDialog.setTitle("Delete the Swrl?");
            confirmDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.permanentlyDelete((Swrl) swrls.get(position));
                    mSectionsPagerAdapter.deletePage(position);
                }
            });
            confirmDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            confirmDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setViewType() {
        viewType = (ViewType) getIntent().getSerializableExtra(EXTRAS_TYPE);
    }

    private void setSwrls() {
        swrls = (ArrayList<?>) getIntent().getSerializableExtra(EXTRAS_SWRLS);
    }

    private void setFirstSwrlIndex() {
        firstSwrlIndex = getIntent().getIntExtra(EXTRAS_INDEX, 0);
    }

    private void setupView() {
        setUpToolbar();
        setupPager();
        setUpRefreshListener();
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setupPager() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        setupViewPager(mSectionsPagerAdapter);
    }

    private void setupViewPager(final SectionsPagerAdapter mSectionsPagerAdapter) {
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(firstSwrlIndex);
        position = firstSwrlIndex;
        currentSwrl = (Swrl) swrls.get(firstSwrlIndex);
        setButton(firstSwrlIndex);
        updateToolbar();
        if (viewType == ADD) {
            new GetSwrlDetails((SwipeRefreshLayout) findViewById(R.id.swiperefresh)).execute(currentSwrl.getDetails().getId());
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                ViewActivity.this.position = position;
                setButton(position);
                currentSwrl = (Swrl) swrls.get(position);
                updateToolbar();
                if (viewType == ADD) {
                    new GetSwrlDetails((SwipeRefreshLayout) findViewById(R.id.swiperefresh)).execute(currentSwrl.getDetails().getId());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                enableDisableSwipeRefresh(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });
    }

    private void enableDisableSwipeRefresh(boolean enable) {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setEnabled(enable);
        }
    }

    private void updateToolbar() {
        ActionBar actionBar = getSupportActionBar();
        int typeColor = currentSwrl.getType().getColor();
        int darkTypeColor = currentSwrl.getType().getDarkColor();
        int titleColor = getResources().getColor(typeColor);
        ColorDrawable drawable = new ColorDrawable(titleColor);
        if (actionBar != null) {
            actionBar.setTitle(currentSwrl.getTitle());
            actionBar.setBackgroundDrawable(drawable);
        }
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(this, darkTypeColor));
    }

    private void setButton(final int position) {
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.viewButton);
        if (viewType != null) {
            switch (viewType) {
                case VIEW:
                    button.setVisibility(View.GONE);
                    break;
                case DONE:
                    button.setVisibility(View.GONE);
                    break;
                case ADD:
                    button.setIcon(R.drawable.ic_add_black_24dp);
                    //noinspection deprecation
                    button.setColorNormal(getResources().getColor(R.color.add));
                    //noinspection deprecation
                    button.setColorPressed(getResources().getColor(R.color.add_pressed));
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            db.save((Swrl) swrls.get(position));
                            Intent homeScreen = new Intent(getApplicationContext(), ListActivity.class);
                            homeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(homeScreen);
                        }
                    });
                    break;
                case ADD_DISCOVER:
                    button.setIcon(R.drawable.ic_add_black_24dp);
                    //noinspection deprecation
                    button.setColorNormal(getResources().getColor(R.color.add));
                    //noinspection deprecation
                    button.setColorPressed(getResources().getColor(R.color.add_pressed));
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            db.save((Swrl) swrls.get(position));
                            mSectionsPagerAdapter.deletePage(position);
                        }
                    });
                    break;
            }
        }
    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ViewPageDetails.newInstance((Swrl) swrls.get(position), swrls, position);
        }

        @Override
        public int getCount() {
            return swrls.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return ((Swrl) swrls.get(position)).getTitle();
        }

        // This is called when notifyDataSetChanged() is called
        @Override
        public int getItemPosition(Object object) {
            // refresh all fragments when data set changed
            return PagerAdapter.POSITION_NONE;
        }

        void deletePage(int position) {
            swrls.remove(position);
            if (swrls.size() == 0) {
                finish();
            } else {
                notifyDataSetChanged();
                Swrl nextCurrent;
                if (swrls.size() <= position) {
                    nextCurrent = (Swrl) swrls.get(swrls.size() - 1);
                } else {
                    nextCurrent = (Swrl) swrls.get(position);
                }
                currentSwrl = nextCurrent;
                updateToolbar();
            }
        }

    }

    private class GetSwrlDetails extends AsyncTask<String, Void, Details> {

        private final SwipeRefreshLayout swipeRefreshLayout;

        private GetSwrlDetails(SwipeRefreshLayout swipeRefreshLayout) {
            this.swipeRefreshLayout = swipeRefreshLayout;
        }

        @Override
        protected Details doInBackground(String... params) {
            String id = params[0];
            Details details = null;

            try {
                Search search = currentSwrl.getType().getSearch();
                details = search.byID(id);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return details;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(Details details) {
            if (details != null) {
                Details originalDetails = currentSwrl.getDetails();
                if (!originalDetails.equals(details)) {
                    Log.d(LOG_TAG, "Updating details for " + currentSwrl.toString());
                    db.saveDetails(currentSwrl, details);
                    currentSwrl.setDetails(details);
                    if (mSectionsPagerAdapter != null) {
                        mSectionsPagerAdapter.notifyDataSetChanged();
                    }
                }
            }
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }

    }
}
