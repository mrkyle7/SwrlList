package co.swrl.swrllist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
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
        setUpViewElements();
    }

    public static void showWhatsNewDialogIfNewVersion(SwrlPreferences preferences, SwrlDialogs dialogs) {
        if (preferences.isPackageNewVersion()) {
            dialogs.buildAndShowWhatsNewDialog();
            preferences.savePackageVersionAsCurrentVersion();
        }
    }

    private void setUpViewElements() {
        setContentView(R.layout.activity_list);
        ArrayAdapter<String> listItems = setUpList();
        setUpInputs(listItems);
    }

    private ArrayAdapter<String> setUpList() {
        ArrayAdapter<String> listItems = new ArrayAdapter<>(this, R.layout.list_item, new ArrayList<String>());
        ListView list = (ListView) findViewById(R.id.itemListView);
        list.setAdapter(listItems);
        return listItems;
    }

    private void setUpInputs(final ArrayAdapter<String> listItems) {
        final Button addItem = (Button) findViewById(R.id.addItemButton);
        final EditText input = (EditText) findViewById(R.id.addItemEditText);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToListIfNotEmptyInput(listItems);
                clearAndFocus(input);
            }
        });
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (enterKeyPressedOrActionDone(actionId, event)) {
                    addItemToListIfNotEmptyInput(listItems);
                    clearAndFocus(input);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void addItemToListIfNotEmptyInput(ArrayAdapter<String> listItems) {
        EditText input = (EditText) findViewById(R.id.addItemEditText);
        String item = String.valueOf(input.getText());
        if (!item.isEmpty()) {
            listItems.insert(item, 0);
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
