package co.swrl.list.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import co.swrl.list.R;
import co.swrl.list.collection.CollectionManager;
import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Details;
import co.swrl.list.item.Search;
import co.swrl.list.item.Swrl;

public class ViewActivity extends AppCompatActivity {

    public static final String LOG_CONTEXT = "VIEW";
    private ArrayList<?> swrls;
    private int firstSwrlIndex;
    private ViewType viewType;
    private CollectionManager db;
    private Swrl currentSwrl;


    public static final String EXTRAS_SWRLS = "swrls";
    public static final String EXTRAS_INDEX = "index";
    public static final String EXTRAS_TYPE = "type";
    private SectionsPagerAdapter mSectionsPagerAdapter;

    public enum ViewType {
        VIEW(),
        ADD();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view, menu);
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
            Log.d("VIEW", "Current Swrl = " + currentSwrl);
            if (currentSwrl.getDetails() != null && currentSwrl.getDetails().getId() != null
                    && !currentSwrl.getDetails().getId().isEmpty()) {
                new GetSwrlDetails().execute(currentSwrl.getDetails().getId());
            }
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
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        currentSwrl = (Swrl) swrls.get(firstSwrlIndex);
        setButton(firstSwrlIndex, mSectionsPagerAdapter);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(currentSwrl.getTitle());
//        int color = getResources().getColor(currentSwrl.getType().getColor());
//        actionBar.setBackgroundDrawable(new ColorDrawable(color));

        if (viewType == ViewType.ADD && currentSwrl.getDetails() != null
                && currentSwrl.getDetails().getId() != null
                && !currentSwrl.getDetails().getId().isEmpty()) {
            new GetSwrlDetails().execute(currentSwrl.getDetails().getId());
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                setButton(position, mSectionsPagerAdapter);
                currentSwrl = (Swrl) swrls.get(position);
                ActionBar actionBar = getSupportActionBar();
                actionBar.setTitle(currentSwrl.getTitle());
//                int color = getResources().getColor(currentSwrl.getType().getColor());
//                actionBar.setBackgroundDrawable(new ColorDrawable(color));
                if (viewType == ViewType.ADD && currentSwrl.getDetails() != null
                        && currentSwrl.getDetails().getId() != null
                        && !currentSwrl.getDetails().getId().isEmpty()) {
                    new GetSwrlDetails().execute(currentSwrl.getDetails().getId());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setButton(final int position, final SectionsPagerAdapter mSectionsPagerAdapter) {
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.viewButton);
        if (viewType != null) {
            switch (viewType) {
                case VIEW:
                    button.setIcon(R.drawable.ic_done_black_24dp);
                    button.setColorNormal(getResources().getColor(R.color.add));
                    button.setColorPressed(getResources().getColor(R.color.add_pressed));
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder confirmDialog = new AlertDialog.Builder(v.getContext());
                            confirmDialog.setTitle("Mark the Swrl as 'done' and markAsDone from list?");
                            confirmDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
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
                        }
                    });
                    break;
                case ADD:
                    button.setIcon(R.drawable.ic_add_black_24dp);
                    button.setColorNormal(getResources().getColor(R.color.add));
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
            }
        }
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

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

        public void deletePage(int position) {
            db.markAsDone((Swrl) swrls.get(position));
            swrls.remove(position);
            if (swrls.size() == 0) {
                finish();
            }
            notifyDataSetChanged();
        }

    }

    private class GetSwrlDetails extends AsyncTask<String, Void, Details> {

        private ProgressDialog progressDialog;

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
            progressDialog = new ProgressDialog(ViewActivity.this);
            progressDialog.setMessage("Updating Details...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Details details) {
            if (details != null) {
                Details originalDetails = currentSwrl.getDetails();
                if (!originalDetails.equals(details)) {
                    Log.d(LOG_CONTEXT, "Updating details for " + currentSwrl.toString());
                    db.saveDetails(currentSwrl, details);
                    currentSwrl.setDetails(details);
                    if (mSectionsPagerAdapter != null) {
                        mSectionsPagerAdapter.notifyDataSetChanged();
                    }
                }
            }
            progressDialog.hide();
        }

    }
}
