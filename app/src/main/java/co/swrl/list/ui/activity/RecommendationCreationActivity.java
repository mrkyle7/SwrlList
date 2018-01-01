package co.swrl.list.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.swrl.list.R;
import co.swrl.list.collection.CollectionManager;
import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.actions.SwrlCoActions;
import co.swrl.list.users.SwrlCoUser;
import co.swrl.list.users.SwrlUserHelpers;
import co.swrl.list.utils.SwrlPreferences;

public class RecommendationCreationActivity extends AppCompatActivity {

    public static final String EXTRAS_SWRL = "swrl";
    private Swrl swrl;
    private SwrlPreferences preferences;
    private List<String> selectedUsers = new ArrayList<>();
    private ArrayAdapter<String> selectedUsersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_creation);
        swrl = (Swrl) getIntent().getSerializableExtra(EXTRAS_SWRL);
        preferences = new SwrlPreferences(this);
        updateToolbar();
        checkUserIsLoggedIn();
        setUpSelectedUserList();
        setUpRecipientInput();
        setCancelButton();
        setCreateRecommendationButton();
    }

    private void setCreateRecommendationButton() {
        Button recommendButton = (Button) findViewById(R.id.recommend_action);
        recommendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText reviewView = (EditText) findViewById(R.id.review_input);
                final String review = reviewView.getText().toString();
                final CollectionManager collectionManager = new SQLiteCollectionManager(RecommendationCreationActivity.this);

                new AsyncTask<Void, Void, Swrl>() {

                    ProgressDialog dialog = new ProgressDialog(RecommendationCreationActivity.this);

                    @Override
                    protected Swrl doInBackground(Void... voids) {
                        return SwrlCoActions.createRecommendation(swrl, review, selectedUsers, preferences, collectionManager);
                    }

                    @Override
                    protected void onPreExecute() {
                        dialog.setMessage("Creating Swrl...");
                        dialog.show();
                    }

                    @Override
                    protected void onPostExecute(final Swrl swrl) {
                        dialog.dismiss();
                        final AlertDialog.Builder sharePrompt = new AlertDialog.Builder(RecommendationCreationActivity.this);
                        sharePrompt.setMessage("Swrl Created!\n"
                        + swrl.getSwrlUrl() + "\nShare to other apps?");
                        sharePrompt.setPositiveButton("share", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                String shareText = swrl.getType().getRecommendationWords() +
                                        " \n"
                                        + swrl.getSwrlUrl()
                                        + "\n"
                                        + swrl.getReview();
                                sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                                RecommendationCreationActivity.this.finish();
                            }
                        });
                        sharePrompt.setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                RecommendationCreationActivity.this.finish();
                            }
                        });
                        sharePrompt.show();
                    }
                }.execute();
            }
        });
    }

    private void setCancelButton() {
        Button cancelButton = (Button) findViewById(R.id.cancel_recommend);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setUpSelectedUserList() {
        ListView selectedUsersList = (ListView) findViewById(R.id.selected_users);
        selectedUsersAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1,
                        selectedUsers);
        selectedUsersList.setAdapter(selectedUsersAdapter);
        selectedUsersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedUsers.remove(i);
                selectedUsersAdapter.notifyDataSetChanged();
            }
        });

    }

    private void setUpRecipientInput() {
        final AutoCompleteTextView recipientInput = (AutoCompleteTextView) findViewById(R.id.recipient_input);
        new AsyncTask<Void, Void, List<SwrlCoUser>>() {
            @Override
            protected List<SwrlCoUser> doInBackground(Void... voids) {
                return SwrlUserHelpers.getUsers();
            }

            @Override
            protected void onPostExecute(List<SwrlCoUser> swrlCoUsers) {
                ArrayList<String> userNames = new ArrayList<>();
                for (SwrlCoUser user : swrlCoUsers) {
                    userNames.add(user.getUsername());
                }
                ArrayAdapter<String> autoCompleteUsers = new ArrayAdapter<>(RecommendationCreationActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        userNames);
                recipientInput.setAdapter(autoCompleteUsers);
            }
        }.execute();
        recipientInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (enterKeyPressedOrActionDone(i, keyEvent)) {
                    addRecipient(recipientInput.getText().toString());
                    recipientInput.setText("");
                    return true;
                }
                return false;
            }
        });
        recipientInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                addRecipient((String) adapterView.getItemAtPosition(i));
                recipientInput.setText("");
            }
        });
    }

    private void addRecipient(String username) {
        selectedUsers.add(username);
        selectedUsersAdapter.notifyDataSetChanged();
    }

    private boolean enterKeyPressedOrActionDone(int actionId, KeyEvent event) {
        return (actionId == EditorInfo.IME_ACTION_DONE) || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN);
    }

    private void checkUserIsLoggedIn() {
        if (!preferences.loggedIn()) {
            AlertDialog.Builder logInPrompt = new AlertDialog.Builder(this);
            logInPrompt.setMessage("You need to be logged in to recommend a Swrl");
            logInPrompt.setPositiveButton("login", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent loginActivity = new Intent(RecommendationCreationActivity.this, LoginActivity.class);
                    startActivity(loginActivity, null);
                }
            });
            logInPrompt.setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    RecommendationCreationActivity.this.finish();
                }
            });
            logInPrompt.show();
        }
    }

    private void updateToolbar() {
        ActionBar actionBar = getSupportActionBar();
        int typeColor = swrl.getType().getColor();
        int darkTypeColor = swrl.getType().getDarkColor();
        int titleColor = getResources().getColor(typeColor);
        ColorDrawable drawable = new ColorDrawable(titleColor);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            String title = "Recommend " +
                    swrl.getTitle() +
                    " to friends";
            actionBar.setTitle(title);
            actionBar.setBackgroundDrawable(drawable);
        }
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(this, darkTypeColor));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
