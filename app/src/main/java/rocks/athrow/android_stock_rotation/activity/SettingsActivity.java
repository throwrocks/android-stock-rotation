package rocks.athrow.android_stock_rotation.activity;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.RealmQueries;

/**
 * SettingsActivity
 * Created by jose on 2/12/17.
 */

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        Preference button = findPreference("delete_database");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Context context = getApplicationContext();
                CharSequence text = "Database deleted!";
                int duration = Toast.LENGTH_SHORT;
                RealmQueries.deleteDatabase(context);
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return true;
            }
        });
    }
}
