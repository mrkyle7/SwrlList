package co.swrl.swrllist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        showWhatsNewDialogIfNewVersion(new SwrlPreferences(this), new SwrlDialogs(this));
    }

    public static void showWhatsNewDialogIfNewVersion(SwrlPreferences preferences, SwrlDialogs dialogs){
        if(preferences.isPackageNewVersion()){
            dialogs.buildAndShowWhatsNewDialog();
            preferences.savePackageVersionAsCurrentVersion();
        }
    }
}
