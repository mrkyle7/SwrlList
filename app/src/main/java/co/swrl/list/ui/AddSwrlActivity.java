package co.swrl.list.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import co.swrl.list.R;
import co.swrl.list.collection.CollectionManager;
import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Details;
import co.swrl.list.item.Search;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class AddSwrlActivity extends AppCompatActivity {

    public static final String EXTRAS_TYPE = "type";
    private Type type;
    private TextView swrlSearchTextView;
    private CollectionManager db;
    private SwrlListAdapter resultsAdapter;
    private ProgressDialog searchProgressDisplay;
    private View noSearchResultsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new SQLiteCollectionManager(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_swrl);
        resultsAdapter = SwrlListAdapter.getResultsListAdapter(this, db);
        searchProgressDisplay = new ProgressDialog(this);
        searchProgressDisplay.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        searchProgressDisplay.setMessage("Searching...");
        setType();
        setTitle();
        setSearchTextView();
        setUpResultsList();
        setUpAddButton();
        setUpCancelButton();
        setUpTitleSearch();
    }

    private void setUpResultsList() {
        ListView resultsList = (ListView) findViewById(R.id.searchResults);
        resultsList.setAdapter(resultsAdapter);
        noSearchResultsText = findViewById(R.id.noSearchResults);
        resultsList.setEmptyView(noSearchResultsText);
        noSearchResultsText.setVisibility(INVISIBLE);
    }

    private void setSearchTextView() {
        swrlSearchTextView = (TextView) findViewById(R.id.addSwrlText);
    }

    private void setType() {
        type = (Type) getIntent().getSerializableExtra(EXTRAS_TYPE);
        type = type == null ? Type.UNKNOWN : type;
    }

    private void setUpTitleSearch() {
        swrlSearchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchQuery = String.valueOf(swrlSearchTextView.getText());
                Button addButton = (Button) findViewById(R.id.add_swrl_button);
                if (searchQuery.isEmpty()) {
                    addButton.setEnabled(false);
                } else {
                    addButton.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        swrlSearchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (enterKeyPressedOrActionDone(actionId, event)) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    new GetSearchResults().execute(String.valueOf(swrlSearchTextView.getText()));
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void setUpCancelButton() {
        Button cancelButton = (Button) findViewById(R.id.cancel_add);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setUpAddButton() {
        Button addButton = (Button) findViewById(R.id.add_swrl_button);
        addButton.setEnabled(false);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = String.valueOf(swrlSearchTextView.getText());
                Swrl swrl = new Swrl(title, type);
                db.save(swrl);
                finish();
            }
        });
    }

    private void setTitle() {
        getSupportActionBar().setTitle("Add a " + type.getFriendlyName());
    }

    private boolean enterKeyPressedOrActionDone(int actionId, KeyEvent event) {
        return (actionId == EditorInfo.IME_ACTION_SEARCH) || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN);
    }

    private class GetSearchResults extends AsyncTask<String, Void, ArrayList<Swrl>> {

        /**
         * Cancel background network operation if we do not have network connectivity.
         */
        @Override
        protected void onPreExecute() {
            //if no network show a warning or something
            resultsAdapter.clear();
            searchProgressDisplay.show();
        }

        @Override
        protected ArrayList<Swrl> doInBackground(String... query) {
            String searchTerm = query[0];
            ArrayList<Swrl> swrls = new ArrayList<>();
            Search search = type.getSearch();
            ArrayList<Details> results = (ArrayList<Details>) search.byTitle(searchTerm);
            if (results != null) {
                for (Details result : results) {
                    Swrl swrl = new Swrl(result.getTitle(), type);
                    swrl.setDetails(result);
                    swrls.add(swrl);
                }
            }
            return swrls;
        }

        @Override
        protected void onPostExecute(ArrayList<Swrl> results) {
            searchProgressDisplay.dismiss();
            noSearchResultsText.setVisibility(VISIBLE);
            resultsAdapter.addAll(results);
        }


        @Override
        protected void onCancelled(ArrayList<Swrl> results) {
            noSearchResultsText.setVisibility(VISIBLE);
            searchProgressDisplay.dismiss();
        }

    }

}
