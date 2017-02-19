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
    public void save(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public void save(@SuppressWarnings("SameParameterValue") String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }
    public void save(@SuppressWarnings("SameParameterValue") String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }

    public String loadString(String key, @SuppressWarnings("SameParameterValue") String defValue) {
        return prefs.getString(key, defValue);
    }
    public boolean loadBoolean(@SuppressWarnings("SameParameterValue") String key) {
        return prefs.getBoolean(key, false);
    }
    public int loadInt(@SuppressWarnings("SameParameterValue") String key) {
        return prefs.getInt(key, 0);
    }

}
