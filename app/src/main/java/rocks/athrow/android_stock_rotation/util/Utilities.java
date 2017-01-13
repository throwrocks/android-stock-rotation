package rocks.athrow.android_stock_rotation.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import static android.view.View.GONE;

/**
 * Utilities
 * Created by jose on 1/10/17.
 */

public final class Utilities {

    private Utilities() {
        throw new AssertionError("No Utilities instances for you!");
    } // suppress constructor

    /**
     * isConnected
     * This method is used to check for network connectivity before attempting a network call
     *
     * @param context the activity from where the method is called
     * @return true for is connected and false for is not connected
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @SuppressWarnings("SameParameterValue")
    public static void showToast(Context context, String message, int duration) {
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }


    /**
     * setViewMode
     * A method to set the layout on view mode
     */
    public static void setViewMode(
            LinearLayout scanItem,
            LinearLayout scanCurrentLocation,
            LinearLayout scanNewLocation,
            EditText currentLocation,
            EditText caseQty,
            EditText looseQty,
            EditText newLocation)
    {
        if (scanItem != null) {
            scanItem.setVisibility(GONE);
        }
        if (scanCurrentLocation != null) {
            scanCurrentLocation.setVisibility(GONE);
        }
        if (scanNewLocation != null) {
            scanNewLocation.setVisibility(GONE);
        }
        if (caseQty != null) {
            caseQty.setEnabled(false);
        }
        if (currentLocation != null) {
            currentLocation.setEnabled(false);
        }
        if (looseQty != null) {
            looseQty.setEnabled(false);
        }
        if (newLocation != null) {
            newLocation.setEnabled(false);
        }
    }

}
