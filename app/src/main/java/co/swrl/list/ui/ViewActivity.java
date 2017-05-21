package co.swrl.list.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import co.swrl.list.R;
import co.swrl.list.collection.CollectionManager;
import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;

public class ViewActivity extends AppCompatActivity {

    private ArrayList<?> swrls;
    private int firstSwrlIndex;
    private ViewType viewType;
    private CollectionManager db;
    public static final String EXTRAS_SWRLS = "swrls";
    public static final String EXTRAS_INDEX = "index";
    public static final String EXTRAS_TYPE = "type";

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

    private void setupPager() {
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        setupViewPager(mSectionsPagerAdapter);
    }

    private void setupViewPager(final SectionsPagerAdapter mSectionsPagerAdapter) {
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(firstSwrlIndex);
        setButton(firstSwrlIndex, mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                setButton(position, mSectionsPagerAdapter);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setButton(final int position, final SectionsPagerAdapter mSectionsPagerAdapter) {
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.viewButton);
        button.setColorNormal(getResources().getColor(R.color.film));
        button.setColorPressed(getResources().getColor(R.color.film));
        if (viewType != null) {
            switch (viewType) {
                case VIEW:
                    button.setIcon(R.drawable.ic_done_black_24dp);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSectionsPagerAdapter.deletePage(position);
                        }
                    });
                    break;
                case ADD:
                    button.setIcon(R.drawable.ic_add_black_24dp);
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
            return PlaceholderFragment.newInstance((Swrl) swrls.get(position));
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

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SWRL = "swrl";

        public PlaceholderFragment() {
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);

            if(isVisibleToUser){
                final Swrl swrl = (Swrl) getArguments().getSerializable(ARG_SWRL);
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                ActionBar actionBar = activity.getSupportActionBar();
                actionBar.setTitle(swrl.getTitle());
            }
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(Swrl swrl) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putSerializable(ARG_SWRL, swrl);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_view, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.title);
            final Swrl swrl = (Swrl) getArguments().getSerializable(ARG_SWRL);
            textView.setText(swrl != null ? swrl.getTitle() : "No Swrls?");
            ImageView poster = (ImageView) rootView.findViewById(R.id.imageView);
            if (swrl != null) {
                Details details = swrl.getDetails();
                if (details == null) {
                    TextView noDetails = (TextView) rootView.findViewById(R.id.noDetails);
                    noDetails.setText(R.string.no_details);
                } else {
                    int iconResource = swrl.getType().getIcon();
                    if (details.getPosterURL() != null && !Objects.equals(details.getPosterURL(), "")) {
                        Picasso.with(getActivity().getBaseContext())
                                .load(details.getPosterURL())
                                .placeholder(R.drawable.progress_spinner)
                                .error(iconResource)
                                .into(poster);
                    } else {
                        poster.setImageResource(iconResource);
                    }
                    TextView categories = (TextView) rootView.findViewById(R.id.categories);
                    if (details.getCategories() != null) {
                        String categoriesString = "Categories: " + TextUtils.join(", ", details.getCategories());
                        categories.setText(categoriesString);
                    }
                }
            }
            return rootView;
        }
    }
}
