package rocks.athrow.android_stock_rotation.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.RealmQueries;
import rocks.athrow.android_stock_rotation.util.PreferencesHelper;
import rocks.athrow.android_stock_rotation.util.Utilities;

import static rocks.athrow.android_stock_rotation.data.Constants.PREF_DELETE_DATABASE;

/**
 * SettingsActivity
 * Created by jose on 2/12/17.
 */

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection deprecation
        addPreferencesFromResource(R.xml.settings);
        @SuppressWarnings("deprecation")
        Preference button = findPreference(PREF_DELETE_DATABASE);
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                deleteDatabase();
                return true;
            }
        });
        PreferencesHelper preferencesHelper = new PreferencesHelper(getApplicationContext());

    }

    private void deleteDatabase(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete all records in the device?")
                .setTitle("Delete Database");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                RealmQueries.deleteDatabase(getApplicationContext());
                Utilities.showToast(getApplicationContext(), "Database deleted!", Toast.LENGTH_SHORT);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
