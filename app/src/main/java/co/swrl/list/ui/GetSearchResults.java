package co.swrl.list.ui;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;

import java.util.ArrayList;

import co.swrl.list.item.Details;
import co.swrl.list.item.Search;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;

import static android.view.View.VISIBLE;

public class GetSearchResults extends AsyncTask<String, Void, ArrayList<Swrl>> {

    private SwrlListAdapter resultsAdapter;
    private ProgressDialog searchProgressDisplay;
    private Type swrlType;
    private View noSearchResultsText;

    GetSearchResults(SwrlListAdapter resultsAdapter, ProgressDialog searchProgressDisplay, Type swrlType, View noSearchResultsText) {
        this.resultsAdapter = resultsAdapter;
        this.searchProgressDisplay = searchProgressDisplay;
        this.swrlType = swrlType;
        this.noSearchResultsText = noSearchResultsText;
    }

    /**
     * Cancel background network operation if we do not have network connectivity.
     */
    @Override
    protected void onPreExecute() {
        //if no network show a warning or something
        resultsAdapter.clear();
        if (searchProgressDisplay != null) {
            searchProgressDisplay.show();
        }
    }

    @Override
    protected ArrayList<Swrl> doInBackground(String... query) {
        String searchTerm = query[0];
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
        if (searchProgressDisplay != null) {
            searchProgressDisplay.dismiss();
        }
        noSearchResultsText.setVisibility(VISIBLE);
        resultsAdapter.addAll(results);
    }


    @Override
    protected void onCancelled(ArrayList<Swrl> results) {
        noSearchResultsText.setVisibility(VISIBLE);
        if (searchProgressDisplay != null) {
            searchProgressDisplay.dismiss();
        }
    }

}
