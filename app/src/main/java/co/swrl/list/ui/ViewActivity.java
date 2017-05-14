package co.swrl.list.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import co.swrl.list.R;
import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;

public class ViewActivity extends AppCompatActivity {

    private ArrayList<?> swrls;
    private int firstSwrlIndex;
    public static final String EXTRAS_SWRLS = "swrls";
    public static final String EXTRAS_INDEX = "index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        setSwrls();
        setFirstSwrlIndex();
        setupView();
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

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                Log.d("DOWNLOAD", "Downloading: " + urlDisplay);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in, null, options);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            try {
                bmImage.setImageBitmap(result);
            } catch (Exception e) {
                Log.d("DOWNLOAD", result.toString());
                e.printStackTrace();
            }
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(((Swrl) swrls.get(firstSwrlIndex)).getTitle());
    }

    private void setupPager() {
        SectionsPagerAdapter mSectionsPagerAdapter = getSectionsPagerAdapter();
        setupViewPager(mSectionsPagerAdapter);
    }

    @NonNull
    private SectionsPagerAdapter getSectionsPagerAdapter() {
        return new SectionsPagerAdapter(getSupportFragmentManager());
    }

    private void setupViewPager(SectionsPagerAdapter mSectionsPagerAdapter) {
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(firstSwrlIndex);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ActionBar supportActionBar = getSupportActionBar();
                if (supportActionBar != null) {
                    supportActionBar.setTitle(((Swrl) swrls.get(position)).getTitle());
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    }

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SWRL = "swrl";

        public PlaceholderFragment() {
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_view, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.title);
            Swrl swrl = (Swrl) getArguments().getSerializable(ARG_SWRL);
            textView.setText(swrl != null ? swrl.getTitle() : "No Swrls?");
            ImageView poster = (ImageView) rootView.findViewById(R.id.imageView);
            if (swrl != null) {
                Details details = swrl.getDetails();
                if (details == null) {
                    TextView noDetails = (TextView) rootView.findViewById(R.id.noDetails);
                    noDetails.setText("No Details found.");
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
