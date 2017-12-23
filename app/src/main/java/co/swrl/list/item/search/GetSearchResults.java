package co.swrl.list.item.search;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;
import co.swrl.list.ui.list.searchresults.SwrlResultsRecyclerAdapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class GetSearchResults extends AsyncTask<String, Void, ArrayList<Swrl>> {

    private final SwrlResultsRecyclerAdapter recyclerAdapter;
    private final ProgressBar progressSpinner;
    private final TextView progressText;
    private final Type swrlType;
    private final View noSearchResultsText;
    private final String LOG_NAME = "GET_SEARCH_RESULTS";

    public GetSearchResults(SwrlResultsRecyclerAdapter resultsAdapter, ProgressBar progressSpinner, TextView progressText, Type swrlType, View noSearchResultsText) {
        this.recyclerAdapter = resultsAdapter;
        this.progressSpinner = progressSpinner;
        this.progressText = progressText;
        this.swrlType = swrlType;
        this.noSearchResultsText = noSearchResultsText;
    }

    /**
     * Cancel background network operation if we do not have network connectivity.
     */
    @Override
    protected void onPreExecute() {
        recyclerAdapter.clear();
        noSearchResultsText.setVisibility(GONE);
        if (progressSpinner != null && progressText != null) {
            progressSpinner.setVisibility(VISIBLE);
            progressText.setVisibility(VISIBLE);
        }
    }

    @Override
    protected ArrayList<Swrl> doInBackground(String... query) {
        String searchTerm = query[0];
        Log.d(LOG_NAME, "Searching for: " + searchTerm);
        ArrayList<Swrl> swrls = new ArrayList<>();
        Search search = swrlType.getSearch();
        ArrayList<Details> results = (ArrayList<Details>) search.byTitle(searchTerm);
        if (results != null) {
            for (Details result : results) {
                Swrl swrl = new Swrl(result.getTitle(), swrlType);
                swrl.setDetails(result);
                swrls.add(swrl);
            }
        }
        return swrls;
    }

    @Override
    protected void onPostExecute(ArrayList<Swrl> results) {
        if (progressSpinner != null && progressText != null) {
            progressSpinner.setVisibility(GONE);
            progressText.setVisibility(GONE);
        }
        if (results == null || results.size() == 0) {
            noSearchResultsText.setVisibility(VISIBLE);
        }
        recyclerAdapter.addAll(results);
    }


    @Override
    protected void onCancelled(ArrayList<Swrl> results) {
        Log.d(LOG_NAME, "I was cancelled");
        if (results == null || results.size() == 0) {
            noSearchResultsText.setVisibility(VISIBLE);
        }
        if (progressSpinner != null && progressText != null) {
            progressSpinner.setVisibility(GONE);
            progressText.setVisibility(GONE);
        }
    }

}
