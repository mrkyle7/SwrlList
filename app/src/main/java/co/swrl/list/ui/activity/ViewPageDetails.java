package co.swrl.list.ui.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import co.swrl.list.R;
import co.swrl.list.collection.CollectionManager;
import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.search.GetSearchResults;
import co.swrl.list.ui.list.SwrlListViewFactory;
import co.swrl.list.ui.list.ViewSwrlResultsRecyclerAdapter;

import static android.view.View.GONE;

public class ViewPageDetails extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SWRL = "swrl";
    private static final String ARG_SWRLS = "swrls";
    private static final String ARG_POSITION = "position";
    private boolean isImageFitToScreen;
    private AsyncTask<String, Void, ArrayList<Swrl>> searchResultsTask;

    public ViewPageDetails() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ViewPageDetails newInstance(Swrl swrl, ArrayList<?> swrls, int position) {
        ViewPageDetails fragment = new ViewPageDetails();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SWRL, swrl);
        args.putSerializable(ARG_SWRLS, swrls);
        args.putSerializable(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final Swrl swrl = (Swrl) getArguments().getSerializable(ARG_SWRL);
        Details details = null;
        if (swrl != null) {
            details = swrl.getDetails();
        }
        final View rootView;
        if (hasNoDetails(details)) {
            rootView = showSearchByTitle(inflater, container, swrl);
        } else {
            rootView = showSwrlDetails(inflater, container, swrl, details);
        }
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (searchResultsTask != null) {
            searchResultsTask.cancel(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchResultsTask != null) {
            searchResultsTask.cancel(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (searchResultsTask != null) {
            searchResultsTask.cancel(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @NonNull
    private View showSwrlDetails(LayoutInflater inflater, ViewGroup container, Swrl swrl, Details details) {
        final View rootView;
        rootView = inflater.inflate(R.layout.fragment_view, container, false);
        final ImageView poster = (ImageView) rootView.findViewById(R.id.imageView);
        int iconResource = swrl.getType().getIcon();

        ImageView background = (ImageView) rootView.findViewById(R.id.imageView2);
        if (details.getPosterURL() != null && !Objects.equals(details.getPosterURL(), "")) {
            Picasso.with(getActivity().getBaseContext())
                    .load(details.getPosterURL())
                    .error(iconResource)
                    .into(background);
        } else {
            background.setImageResource(iconResource);
        }
        if (details.getPosterURL() != null && !Objects.equals(details.getPosterURL(), "")) {
            Picasso.with(getActivity().getBaseContext())
                    .load(details.getPosterURL())
                    .placeholder(R.drawable.progress_spinner)
                    .error(iconResource)
                    .into(poster, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            resizeView(poster, 150, rootView);
                        }
                    });
        } else {
            poster.setImageResource(iconResource);
        }

        final RelativeLayout imageContainer = (RelativeLayout) rootView.findViewById(R.id.image_background);
        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isImageFitToScreen) {
                    isImageFitToScreen = false;
                    imageContainer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) getContext().getResources().getDimension(R.dimen.viewImageHeight)));
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    poster.setLayoutParams(layoutParams);
                    poster.setAdjustViewBounds(true);
                } else {
                    isImageFitToScreen = true;
                    imageContainer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                    poster.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                    poster.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
            }
        });
        TextView titleText = (TextView) rootView.findViewById(R.id.title);
        String title = swrl.getTitle();
        titleText.setText(title);

        TextView keyDetails = (TextView) rootView.findViewById(R.id.keyDetails);

        String keyDetailsText = null;

        String separator = " | ";
        switch (swrl.getType()) {
            case FILM:
                String director = details.getCreator() == null ? "" : details.getCreator() + separator;
                String genres = details.getCategories() == null ? "" : TextUtils.join(", ", details.getCategories()) + separator;
                String actors = details.getActors() == null ? "" : details.getActors() + separator;
                String runtime = details.getRuntime() == null ? "" : details.getRuntime() + separator;
                keyDetailsText = director + genres + actors + runtime;
                break;
            case TV:
                break;
            case BOOK:
                break;
            case ALBUM:
                break;
            case VIDEO_GAME:
                break;
            case BOARD_GAME:
                break;
            case APP:
                break;
            case PODCAST:
                break;
            case UNKNOWN:
                break;
        }


        if (keyDetailsText != null) {
            keyDetails.setText(keyDetailsText);
        } else {
            keyDetails.setVisibility(GONE);
        }

        TextView ratings = (TextView) rootView.findViewById(R.id.ratings);
        if (details.getRatings() != null) {
            String ratingsString = "Ratings: ";

            for (Details.Ratings rating : details.getRatings()) {
                ratingsString += rating.getSource().equals("Internet Movie Database") ? "IMDB" : rating.getSource();
                ratingsString += ": ";
                ratingsString += rating.getValue();
                ratingsString += "; ";
            }
            ratings.setText(ratingsString);
        } else {
            ratings.setVisibility(GONE);
        }

        TextView overviewText = (TextView) rootView.findViewById(R.id.overview);
        if (details.getOverview() != null && !details.getOverview().isEmpty()) {
            overviewText.setText(Html.fromHtml(details.getOverview()));
        } else {
            overviewText.setVisibility(GONE);
        }
        return rootView;
    }

    @NonNull
    private View showSearchByTitle(LayoutInflater inflater, ViewGroup container, final Swrl swrl) {
        View rootView;
        rootView = inflater.inflate(R.layout.activity_add_swrl, container, false);
        CollectionManager db = new SQLiteCollectionManager(getActivity());
        final TextView noSearchResultsText = (TextView) rootView.findViewById(R.id.noSearchResults);
        final TextView progressText = (TextView) rootView.findViewById(R.id.progressSearchingText);
        final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        ArrayList<Swrl> originalSwrls = getSwrlsFromArgs();
        int position = getArguments().getInt(ARG_POSITION);

        removeFooter(rootView);

        final ViewSwrlResultsRecyclerAdapter resultsAdapter = setUpSearchResults(swrl, rootView, db, noSearchResultsText, progressText, progressBar, originalSwrls, position);

        final TextView swrlSearchTextView = setUpSearchTextView(swrl, rootView, noSearchResultsText, progressText, progressBar, resultsAdapter);

        searchResultsTask = new GetSearchResults(resultsAdapter, progressBar, progressText, swrl.getType(), noSearchResultsText).execute(String.valueOf(swrlSearchTextView.getText()));
        return rootView;
    }

    @NonNull
    private ViewSwrlResultsRecyclerAdapter setUpSearchResults(Swrl swrl, View rootView, CollectionManager db, TextView noSearchResultsText, TextView progressText, ProgressBar progressBar, ArrayList<Swrl> originalSwrls, int position) {
        final ViewSwrlResultsRecyclerAdapter resultsAdapter = new ViewSwrlResultsRecyclerAdapter(getActivity(), db, originalSwrls, swrl, position);
        SwrlListViewFactory.setUpListView(getActivity(), (RecyclerView) rootView.findViewById(R.id.listView), resultsAdapter);

        noSearchResultsText.setVisibility(GONE);
        progressText.setVisibility(GONE);
        progressBar.setVisibility(GONE);
        return resultsAdapter;
    }

    @NonNull
    private TextView setUpSearchTextView(final Swrl swrl, View rootView, final TextView noSearchResultsText, final TextView progressText, final ProgressBar progressBar, final ViewSwrlResultsRecyclerAdapter resultsAdapter) {
        final TextView swrlSearchTextView = (TextView) rootView.findViewById(R.id.addSwrlText);
        swrlSearchTextView.append(swrl.getTitle());

        swrlSearchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (enterKeyPressedOrActionDone(actionId, event)) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    searchResultsTask.cancel(true);
                    searchResultsTask = new GetSearchResults(resultsAdapter, progressBar, progressText, swrl.getType(), noSearchResultsText).execute(String.valueOf(swrlSearchTextView.getText()));
                    return true;
                } else {
                    return false;
                }
            }
        });
        return swrlSearchTextView;
    }

    private void removeFooter(View rootView) {
        LinearLayout footer = (LinearLayout) rootView.findViewById(R.id.footer);
        footer.setVisibility(GONE);
    }

    private boolean hasNoDetails(Details details) {
        return details == null || details.getId() == null || details.getId().isEmpty();
    }

    @NonNull
    private ArrayList<Swrl> getSwrlsFromArgs() {
        ArrayList<?> swrlsFromArgs = (ArrayList<?>) getArguments().getSerializable(ARG_SWRLS);
        ArrayList<Swrl> originalSwrls = new ArrayList<>();
        if (swrlsFromArgs != null) {
            for (Object swrlFromArg : swrlsFromArgs) {
                originalSwrls.add((Swrl) swrlFromArg);
            }
        }
        return originalSwrls;
    }

    private boolean enterKeyPressedOrActionDone(int actionId, KeyEvent event) {
        return (actionId == EditorInfo.IME_ACTION_SEARCH) || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN);
    }

    private void resizeView(ImageView view, int size, View rootView) {
        view.getLayoutParams().height = getDPI(size, rootView);
        view.getLayoutParams().width = getDPI(size, rootView);
    }

    private int getDPI(int i, View rootView) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, rootView.getContext().getResources().getDisplayMetrics());
    }
}