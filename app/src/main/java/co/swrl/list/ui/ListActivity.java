package co.swrl.list.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.HashMap;
import java.util.Map;

import co.swrl.list.R;
import co.swrl.list.SwrlPreferences;
import co.swrl.list.collection.CollectionManager;
import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Type;

public class ListActivity extends AppCompatActivity {

    private SwrlListAdapter swrlListAdapter;
    private SQLiteCollectionManager collectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showWhatsNewDialogIfNewVersion(new SwrlPreferences(this), new SwrlDialogs(this));
        collectionManager = new SQLiteCollectionManager(this);
        setUpViewElements(collectionManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        swrlListAdapter.clear();
        swrlListAdapter.addAll(collectionManager.getActive());
        FloatingActionsMenu addSwrlMenu = (FloatingActionsMenu) findViewById(R.id.addItemFAB);
        addSwrlMenu.collapseImmediately();
    }

    @Override
    public void onBackPressed() {
        FloatingActionsMenu addSwrlMenu = (FloatingActionsMenu) findViewById(R.id.addItemFAB);
        if (addSwrlMenu.isExpanded()) {
            addSwrlMenu.collapse();
        } else {
            super.onBackPressed();
        }
    }


    public static void showWhatsNewDialogIfNewVersion(SwrlPreferences preferences, SwrlDialogs dialogs) {
        if (preferences.isPackageNewVersion()) {
            dialogs.buildAndShowWhatsNewDialog();
            preferences.savePackageVersionAsCurrentVersion();
        }
    }

    private void setUpViewElements(CollectionManager collectionManager) {
        setContentView(R.layout.activity_list);
        getSupportActionBar().setTitle("Swrl List");
        swrlListAdapter = SwrlListAdapter.getActiveListAdapter(this, collectionManager);
        setUpList();
        setUpAddSwrlButtons();
    }

    private void setUpList() {
        ListView list = (ListView) findViewById(R.id.itemListView);
        list.setAdapter(swrlListAdapter);
        list.setEmptyView(findViewById(R.id.noSwrlsText));
    }

    private void setUpAddSwrlButtons() {
        HashMap<Integer, Type> addButtons = new HashMap<>();
        addButtons.put(R.id.add_unknown, Type.UNKNOWN);
        addButtons.put(R.id.add_film, Type.FILM);
        addButtons.put(R.id.add_album, Type.ALBUM);
        addButtons.put(R.id.add_board_game, Type.BOARD_GAME);
        addButtons.put(R.id.add_tv, Type.TV);
        addButtons.put(R.id.add_book, Type.BOOK);

        for (final Map.Entry<Integer, Type> button : addButtons.entrySet()) {
            FloatingActionButton actionButton = (FloatingActionButton) findViewById(button.getKey());
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addSwrlActivity = new Intent(getApplicationContext(), AddSwrlActivity.class);
                    addSwrlActivity.putExtra(AddSwrlActivity.EXTRAS_TYPE, button.getValue());
                    startActivity(addSwrlActivity, null);
                }
            });
        }
    }
}
