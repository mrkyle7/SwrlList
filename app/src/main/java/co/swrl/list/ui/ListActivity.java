package co.swrl.list.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import co.swrl.list.R;
import co.swrl.list.SwrlPreferences;
import co.swrl.list.collection.CollectionManager;
import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;

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
        ActiveListAdapter swrlRows = new ActiveListAdapter(this, R.layout.list_row, collectionManager);
        setUpList(swrlRows);
        setUpInputs(swrlRows);
    }

    private void setUpList(final ActiveListAdapter swrlRows) {
        ListView list = (ListView) findViewById(R.id.itemListView);
        list.setAdapter(swrlRows);
    }

    private void setUpInputs(final ActiveListAdapter swrlRows) {
        final ImageButton addItem = (ImageButton) findViewById(R.id.addItemButton);
        final EditText input = (EditText) findViewById(R.id.addItemEditText);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu typeSelector = new PopupMenu(getApplicationContext(), findViewById(R.id.addItemButton));
                typeSelector.inflate(R.menu.type_selector);
                for (Type type: Type.values()){
                    typeSelector.getMenu().add(type.toString());
                }
                typeSelector.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        addItemToListAndPersistIfNewAndNotEmptyInput(swrlRows, Type.valueOf(item.toString()));
                        clearAndFocus(input);
                        return true;
                    }
                });
                typeSelector.show();
            }
        });
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (enterKeyPressedOrActionDone(actionId, event)) {
                    addItemToListAndPersistIfNewAndNotEmptyInput(swrlRows, null);
                    clearAndFocus(input);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void addItemToListAndPersistIfNewAndNotEmptyInput(ActiveListAdapter swrlRows, Type type) {
        EditText input = (EditText) findViewById(R.id.addItemEditText);
        String title = String.valueOf(input.getText());
        Swrl swrl = new Swrl(title, type);
        if (titleIsNotBlank(title) && swrlIsNew(swrlRows, swrl)) {
            swrlRows.insert(swrl, 0);
        }
    }

    private boolean titleIsNotBlank(String title) {
        return !title.isEmpty();
    }

    private boolean swrlIsNew(ActiveListAdapter swrlRows, Swrl swrl) {
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
