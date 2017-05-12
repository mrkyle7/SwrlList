package co.swrl.list.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import co.swrl.list.R;
import co.swrl.list.collection.CollectionManager;
import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;

public class AddSwrlActivity extends AppCompatActivity {

    public static final String EXTRAS_TYPE = "type";
    private Type type;
    private TextView swrlSearchTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final CollectionManager db = new SQLiteCollectionManager(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_swrl);
        setSearchTextView();
        setType();
        setTitle();
        setUpAddButton(db);
        setUpCancelButton();
        setUpTitleSearch();
    }

    private void setSearchTextView() {
        swrlSearchTextView = (TextView) findViewById(R.id.addSwrlText);
    }

    private void setType() {
        type = (Type) getIntent().getSerializableExtra(EXTRAS_TYPE);
    }

    private void setUpTitleSearch() {
        swrlSearchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchQuery = String.valueOf(swrlSearchTextView.getText());
                Button addButton = (Button) findViewById(R.id.add_swrl_button);
                if (searchQuery.isEmpty()){
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
                if (enterKeyPressedOrActionDone(actionId, event)){
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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

    private void setUpAddButton(final CollectionManager db) {
        Button addButton = (Button) findViewById(R.id.add_swrl_button);
        addButton.setEnabled(false);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = String.valueOf(swrlSearchTextView.getText());
                type = type == null ? Type.UNKNOWN : type;
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
}
