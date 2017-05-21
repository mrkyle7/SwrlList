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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ListActivity extends AppCompatActivity {

    private SwrlListAdapter swrlListAdapter;
    private SQLiteCollectionManager collectionManager;
    private boolean showingMainButtons = true;

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
        getSupportActionBar().setTitle(R.string.app_title);
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
        final HashMap<Integer, Type> mainButtons = new HashMap<>();
        mainButtons.put(R.id.add_film, Type.FILM);
        mainButtons.put(R.id.add_album, Type.ALBUM);
        mainButtons.put(R.id.add_board_game, Type.BOARD_GAME);
        mainButtons.put(R.id.add_tv, Type.TV);
        mainButtons.put(R.id.add_book, Type.BOOK);

        final HashMap<Integer, Type> otherButtons = new HashMap<>();
        otherButtons.put(R.id.add_podcast, Type.PODCAST);
        otherButtons.put(R.id.add_phone_app, Type.APP);
        otherButtons.put(R.id.add_video_game, Type.VIDEO_GAME);

        enableButtons(mainButtons);
        disableButtons(otherButtons);

        FloatingActionButton moreButton = (FloatingActionButton) findViewById(R.id.add_unknown);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showingMainButtons){
                    disableButtons(mainButtons);
                    enableButtons(otherButtons);
                    showingMainButtons = false;
                } else {
                    disableButtons(otherButtons);
                    enableButtons(mainButtons);
                    showingMainButtons = true;
                }
            }
        });

    }

    private void enableButtons(HashMap<Integer, Type> mainButtons) {
        for (final Map.Entry<Integer, Type> button : mainButtons.entrySet()) {
            FloatingActionButton actionButton = (FloatingActionButton) findViewById(button.getKey());
            actionButton.setVisibility(VISIBLE);
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
    private void disableButtons(HashMap<Integer, Type> buttons) {
        for (final Map.Entry<Integer, Type> button : buttons.entrySet()) {
            FloatingActionButton actionButton = (FloatingActionButton) findViewById(button.getKey());
            actionButton.setVisibility(GONE);
        }
    }
}
