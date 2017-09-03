package co.swrl.list.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import co.swrl.list.R;
import co.swrl.list.collection.CollectionManager;
import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;
import co.swrl.list.item.search.GetSearchResults;
import co.swrl.list.ui.list.AddSwrlResultsRecyclerAdapter;
import co.swrl.list.ui.list.SwrlListViewFactory;

import static android.view.View.GONE;

public class AddSwrlActivity extends AppCompatActivity {

    public static final String EXTRAS_TYPE = "swrlType";
    private Type swrlType;
    private TextView swrlSearchTextView;
    private CollectionManager db;
    private AddSwrlResultsRecyclerAdapter resultsAdapter;
    private View noSearchResultsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new SQLiteCollectionManager(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_swrl);
        resultsAdapter = new AddSwrlResultsRecyclerAdapter(this, db);
        setType();
        setTitle();
        setSearchTextView();
        hideProgressDetails();
        setUpResultsList();
        setNoResultsText();
        setUpAddButton();
        setUpCancelButton();
        setUpTitleSearch();
    }

    private void hideProgressDetails() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        TextView progressText = (TextView) findViewById(R.id.progressSearchingText);

        progressBar.setVisibility(GONE);
        progressText.setVisibility(GONE);
    }

    private void setUpResultsList() {
        SwrlListViewFactory.setUpListView(this, (RecyclerView) findViewById(R.id.listView), resultsAdapter);
    }

    private void setNoResultsText() {
        noSearchResultsText = findViewById(R.id.noSearchResults);
        noSearchResultsText.setVisibility(GONE);
    }

    private void setSearchTextView() {
        swrlSearchTextView = (TextView) findViewById(R.id.addSwrlText);
    }

    private void setType() {
        swrlType = (Type) getIntent().getSerializableExtra(EXTRAS_TYPE);
        swrlType = swrlType == null ? Type.UNKNOWN : swrlType;
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
                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                    TextView progressText = (TextView) findViewById(R.id.progressSearchingText);
                    new GetSearchResults(resultsAdapter, progressBar, progressText, swrlType, noSearchResultsText).execute(String.valueOf(swrlSearchTextView.getText()));
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
                Swrl swrl = new Swrl(title, swrlType);
                db.save(swrl);
                finish();
            }
        });
    }

    private void setTitle() {
        getSupportActionBar().setTitle("Add a " + swrlType.getFriendlyName());
    }

    private boolean enterKeyPressedOrActionDone(int actionId, KeyEvent event) {
        return (actionId == EditorInfo.IME_ACTION_SEARCH) || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN);
    }

}
