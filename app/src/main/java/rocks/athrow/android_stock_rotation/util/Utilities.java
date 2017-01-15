package rocks.athrow.android_stock_rotation.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.zxing.IntentIntegrator;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
     * Pass null for views that are not needed
     */
    public static void setViewMode(
            LinearLayout scanItem,
            LinearLayout scanCurrentLocation,
            LinearLayout scanNewLocation,
            EditText currentLocation,
            EditText caseQty,
            EditText looseQty,
            EditText newLocation) {
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

    /**
     * setEditMopde
     *
     * @param scanItem
     * @param scanCurrentLocation
     * @param scanNewLocation
     * @param currentLocation
     * @param caseQty
     * @param looseQty
     * @param newLocation
     */
    public static void setEditMode(
            LinearLayout scanItem,
            LinearLayout scanCurrentLocation,
            LinearLayout scanNewLocation,
            EditText currentLocation,
            EditText caseQty,
            EditText looseQty,
            EditText newLocation) {
        if (scanItem != null) {
            scanItem.setVisibility(VISIBLE);
        }
        if (scanCurrentLocation != null) {
            scanCurrentLocation.setVisibility(VISIBLE);
        }
        if (scanNewLocation != null) {
            scanNewLocation.setVisibility(VISIBLE);
        }
        if (caseQty != null) {
            caseQty.setEnabled(true);
        }
        if (currentLocation != null) {
            looseQty.setEnabled(true);
        }
        if (looseQty != null) {
            looseQty.setEnabled(false);
        }
        if (newLocation != null) {
            newLocation.setEnabled(true);
        }
    }

    /**
     * setItemViews
     * A method to set the item views
     *
     * @param sku          the item sku
     * @param description  the item description
     * @param packSize     the item packsize
     * @param receivedDate the item received date
     */
    public static void setItemViews(
            TextView inputItemSku,
            TextView inputItemDescription,
            TextView inputPackSize,
            TextView inputReceivedDate,
            String sku,
            String description,
            String packSize,
            String receivedDate) {
        inputItemSku.setText(sku);
        inputItemDescription.setText(description);
        inputPackSize.setText(packSize);
        inputReceivedDate.setText(receivedDate);
    }

    /**
     * setQtys
     * A method to set the item qtys
     *
     * @param caseQty  the case qty
     * @param looseQty the loose qty
     */
    public static void setQtys(
            TextView inputCaseQty,
            TextView inputLooseQty,
            String caseQty,
            String looseQty) {
        inputCaseQty.setText(caseQty);
        inputLooseQty.setText(looseQty);
    }

    /**
     * setCurrentLocationView
     *
     * @param location
     */
    public static void setCurrentLocationView(
            TextView inputCurrentLocation,
            String location) {
        inputCurrentLocation.setText(location);
    }

    /**
     * setNewLocation
     *
     * @param location
     */
    public static void setNewLocationView(
            TextView inputNewLocation,
            String location) {
        inputNewLocation.setText(location);
    }

}
