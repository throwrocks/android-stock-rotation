package rocks.athrow.android_stock_rotation.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.joselopezrosario.androidfm.FmData;
import com.joselopezrosario.androidfm.FmRecord;
import com.joselopezrosario.androidfm.FmResponse;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.api.API;
import rocks.athrow.android_stock_rotation.data.RealmQueries;
import rocks.athrow.android_stock_rotation.util.PreferencesHelper;
import rocks.athrow.android_stock_rotation.util.Utilities;

import static rocks.athrow.android_stock_rotation.data.Constants.EMPTY;
import static rocks.athrow.android_stock_rotation.data.Constants.LAST_SYNC;
import static rocks.athrow.android_stock_rotation.data.Constants.LOCATIONS_FILTER;
import static rocks.athrow.android_stock_rotation.data.Constants.LOCATIONS_FILTER_PRIMARY_ONLY;
import static rocks.athrow.android_stock_rotation.data.Constants.LOCATIONS_FILTER_ROW;
import static rocks.athrow.android_stock_rotation.data.Constants.LOCATIONS_FILTER_ROW_INDEX;
import static rocks.athrow.android_stock_rotation.data.Constants.PREF_DELETE_DATABASE;
import static rocks.athrow.android_stock_rotation.data.Constants.SEARCH_CRITERIA;
import static rocks.athrow.android_stock_rotation.data.Constants.SETTINGS_API_KEY;
import static rocks.athrow.android_stock_rotation.data.Constants.SETTINGS_EMPLOYEE_NAME;
import static rocks.athrow.android_stock_rotation.data.Constants.SETTINGS_EMPLOYEE_NUMBER;
import static rocks.athrow.android_stock_rotation.data.Constants.UNKNOWN;

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        EditTextPreference prefApiKey = (EditTextPreference) getPreferenceScreen().findPreference(SETTINGS_API_KEY);
        EditTextPreference prefEmployeeNumber = (EditTextPreference) getPreferenceScreen().findPreference(SETTINGS_EMPLOYEE_NUMBER);
        EditTextPreference prefEmployeeName = (EditTextPreference) getPreferenceScreen().findPreference(SETTINGS_EMPLOYEE_NAME);
        String apiKey = prefApiKey.getText();
        String employeeNumber = prefEmployeeNumber.getText();
        String employeeName = prefEmployeeName.getText();
        //noinspection deprecation
        getPreferenceScreen().findPreference(SETTINGS_API_KEY).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ValidateKeyTask validateKeyTask = new ValidateKeyTask(getApplicationContext());
                String apiKey = newValue.toString();
                if (!apiKey.isEmpty()) {
                    validateKeyTask.execute(newValue.toString());
                } else {
                    updatePreference(SETTINGS_EMPLOYEE_NAME, EMPTY);
                    updatePreference(SETTINGS_EMPLOYEE_NUMBER, EMPTY);
                }
                updatePreference(SETTINGS_API_KEY, newValue.toString());
                return false;
            }
        });
        updatePreference(SETTINGS_API_KEY, apiKey);
        updatePreference(SETTINGS_EMPLOYEE_NUMBER, employeeNumber);
        updatePreference(SETTINGS_EMPLOYEE_NAME, employeeName);
    }

    private void updatePreference(String key, String newValue) {
        //noinspection deprecation
        Preference preference = findPreference(key);
        EditTextPreference editTextPreference = (EditTextPreference) preference;
        PreferencesHelper preferencesHelper = new PreferencesHelper(getApplicationContext());
        preferencesHelper.save(key, newValue);
        if (newValue != null) {
            editTextPreference.setText(newValue);
            if (newValue.isEmpty()) {
                editTextPreference.setSummary(UNKNOWN);
            } else {
                editTextPreference.setSummary(newValue);
            }
        }
    }


    private void deleteDatabase() {
        final Resources res = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(res.getString(R.string.delete_all_records))
                .setTitle(res.getString(R.string.delete_database));
        builder.setPositiveButton(res.getString(R.string.delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                RealmQueries.deleteDatabase(getApplicationContext());
                Utilities.showToast(getApplicationContext(), res.getString(R.string.database_deleted), Toast.LENGTH_SHORT);
                PreferencesHelper prefs = new PreferencesHelper(getApplicationContext());
                prefs.deleteKey(LOCATIONS_FILTER);
                prefs.deleteKey(LOCATIONS_FILTER_PRIMARY_ONLY);
                prefs.deleteKey(LOCATIONS_FILTER_ROW);
                prefs.deleteKey(LOCATIONS_FILTER_ROW_INDEX);
                prefs.deleteKey(SEARCH_CRITERIA);
                prefs.deleteKey(LAST_SYNC);
            }
        });
        builder.setNegativeButton(res.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class ValidateKeyTask extends AsyncTask<String, Void, String[]> {
        final Context context;

        ValidateKeyTask(Context context) {
            this.context = context;
        }

        @Override
        protected String[] doInBackground(String... params) {
            String[] result = new String[3];
            int key = Integer.parseInt(params[0]);
            FmResponse response = API.validateKey(key);
            if (response == null) {
                return result;
            }
            String scriptResult = response.getScriptResult();
            if (scriptResult.equals("Ok")) {
                FmData data = new FmData(response);
                if (data.size() == 1) {
                    FmRecord item = data.getRecord(0);
                    String employeeNumber = item.getString(SETTINGS_EMPLOYEE_NUMBER);
                    String employeeName = item.getString(SETTINGS_EMPLOYEE_NAME);
                    result[0] = employeeNumber;
                    result[1] = employeeName;
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            Resources res = getResources();
            String employeeNumber = result[0];
            String employeeName = result[1];
            boolean valid;
            if (employeeName == null) {
                valid = false;
            } else if (employeeName.isEmpty()) {
                valid = false;
            } else if (employeeNumber == null) {
                valid = false;
            } else if (employeeNumber.isEmpty()) {
                valid = false;
            } else {
                valid = true;
            }
            if (!valid) {
                updatePreference(SETTINGS_EMPLOYEE_NAME, EMPTY);
                updatePreference(SETTINGS_EMPLOYEE_NUMBER, EMPTY);
                Utilities.showToast(getApplicationContext(),
                        res.getString(R.string.api_key_not_found), Toast.LENGTH_SHORT);
            } else {
                updatePreference(SETTINGS_EMPLOYEE_NAME, employeeName);
                updatePreference(SETTINGS_EMPLOYEE_NUMBER, employeeNumber);
                Utilities.showToast(getApplicationContext(),
                        res.getString(R.string.api_key_validated), Toast.LENGTH_SHORT);
            }
        }
    }
}
