package co.swrl.swrllist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showWhatsNewDialogIfNewVersion(new SwrlPreferences(this), new SwrlDialogs(this));
        setUpViewElements(new SqlLiteCollectionManager());
    }

    public static void showWhatsNewDialogIfNewVersion(SwrlPreferences preferences, SwrlDialogs dialogs) {
        if (preferences.isPackageNewVersion()) {
            dialogs.buildAndShowWhatsNewDialog();
            preferences.savePackageVersionAsCurrentVersion();
        }
    }

    private void setUpViewElements(CollectionManager collectionManager) {
        setContentView(R.layout.activity_list);
        ArrayList<Swrl> swrls = (ArrayList<Swrl>) collectionManager.getSwrls();
        SwrlRowAdapter swrlRows = new SwrlRowAdapter(this, R.layout.list_item, swrls);
        setUpList(swrlRows);
        setUpInputs(swrlRows);
    }

    private void setUpList(SwrlRowAdapter swrlRows) {
        ListView list = (ListView) findViewById(R.id.itemListView);
        list.setAdapter(swrlRows);
    }

    private void setUpInputs(final SwrlRowAdapter swrlRows) {
        final Button addItem = (Button) findViewById(R.id.addItemButton);
        final EditText input = (EditText) findViewById(R.id.addItemEditText);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToListIfNotEmptyInput(swrlRows);
                clearAndFocus(input);
            }
        });
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (enterKeyPressedOrActionDone(actionId, event)) {
                    addItemToListIfNotEmptyInput(swrlRows);
                    clearAndFocus(input);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void addItemToListIfNotEmptyInput(SwrlRowAdapter swrlRows) {
        EditText input = (EditText) findViewById(R.id.addItemEditText);
        String title = String.valueOf(input.getText());
        if (!title.isEmpty()) {
            swrlRows.insert(new Swrl(title), 0);
        }
    }

    private void clearAndFocus(EditText input) {
        input.requestFocus();
        input.setText("");
    }

    private boolean enterKeyPressedOrActionDone(int actionId, KeyEvent event) {
        return (actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN));
    }
}
