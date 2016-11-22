package co.swrl.list;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showWhatsNewDialogIfNewVersion(new SwrlPreferences(this), new SwrlDialogs(this));
        setUpViewElements(new SQLiteCollectionManager(this));
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
        SwrlRowAdapter swrlRows = new SwrlRowAdapter(this, R.layout.list_row, swrls, collectionManager);
        setUpList(swrlRows);
        setUpInputs(swrlRows, collectionManager);
    }

    private void setUpList(SwrlRowAdapter swrlRows) {
        ListView list = (ListView) findViewById(R.id.itemListView);
        list.setAdapter(swrlRows);
    }

    private void setUpInputs(final SwrlRowAdapter swrlRows, final CollectionManager collectionManager) {
        final ImageButton addItem = (ImageButton) findViewById(R.id.addItemButton);
        final EditText input = (EditText) findViewById(R.id.addItemEditText);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToListAndPersistIfNewAndNotEmptyInput(swrlRows, collectionManager);
                clearAndFocus(input);
            }
        });
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (enterKeyPressedOrActionDone(actionId, event)) {
                    addItemToListAndPersistIfNewAndNotEmptyInput(swrlRows, collectionManager);
                    clearAndFocus(input);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void addItemToListAndPersistIfNewAndNotEmptyInput(SwrlRowAdapter swrlRows, CollectionManager collectionManager) {
        EditText input = (EditText) findViewById(R.id.addItemEditText);
        String title = String.valueOf(input.getText());
        Swrl swrl = new Swrl(title);
        if (titleIsNotBlank(title) && swrlIsNew(swrlRows, swrl)) {
            swrlRows.insert(swrl, 0);
            collectionManager.saveSwrl(swrl);
        }
    }

    private boolean titleIsNotBlank(String title) {
        return !title.isEmpty();
    }

    private boolean swrlIsNew(SwrlRowAdapter swrlRows, Swrl swrl) {
        return swrlRows.getPosition(swrl) == -1;
    }

    private void clearAndFocus(EditText input) {
        input.requestFocus();
        input.setText("");
    }

    private boolean enterKeyPressedOrActionDone(int actionId, KeyEvent event) {
        return (actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN));
    }
}
