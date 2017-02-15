package rocks.athrow.android_stock_rotation.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * PreferencesHelper
 * Created by jose on 1/10/17.
 */

public class PreferencesHelper {

    private final SharedPreferences prefs;

    private static final String FILE_NAME = "rocks.throw.service_tickets.preferences";

    public PreferencesHelper(Context context) {
        prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Save the specified value to the shared preferences
     *
     * @param key   The key of the value you wish to load
     * @param value The value to store
     */
    public void save(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public void save(@SuppressWarnings("SameParameterValue") String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    /**
     * Load the specified value from the shared preferences
     *
     * @param key      The key of the value you wish to load
     * @param defValue The default value to be returned if no value is found
     */
    public String loadString(String key, @SuppressWarnings("SameParameterValue") String defValue) {
        return prefs.getString(key, defValue);
    }
    public boolean loadBoolean(@SuppressWarnings("SameParameterValue") String key) {
        return prefs.getBoolean(key, false);
    }
}
