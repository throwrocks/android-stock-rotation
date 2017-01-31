package rocks.athrow.android_stock_rotation.activity;

import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by joselopez on 1/31/17.
 */

public class TransactionBaseActivity extends AppCompatActivity {
    public static final String ITEM_ID = "item_id";
    public static final String TRANSACTION_ID = "transaction_id";
    public static final String MODE = "mode";
    public static final String MODE_EDIT = "edit";
    public static final String MODE_VIEW = "view";
    public static final String SCAN_ITEM = "item";
    public static final String SCAN_CURRENT_LOCATION = "currentLocation";
    public static final String SCAN_NEW_LOCATION = "newLocation";
    public static final String IN = "in";
    public static final String OUT = "out";

    public TextView mInputItemSku;
    public TextView mInputItemDescription;
    public TextView mInputTagNumber;
    public TextView mInputPackSize;
    public TextView mInputReceivedDate;
    public TextView mInputExpirationDate;
    public String mBarcodeContents;
    public String mScanType;
    public String mMode;
    public String mTransactionId;
    public String mItemId;
    public int mReceivingId;
    public LinearLayout mButtonScanItem;
    public LinearLayout mButtonScanCurrentLocation;
    public LinearLayout mButtonScanNewLocation;
    public EditText mCurrentLocationView;
    public EditText mCaseQtyView;
    public EditText mNewLocationView;
    public LinearLayout mButtonCommit;

    /**
     *
     * @param scanItem
     * @param scanCurrentLocation
     * @param scanNewLocation
     * @param commitButton
     * @param currentLocation
     * @param caseQty
     * @param newLocation
     */
    public static void setViewMode(
            LinearLayout scanItem,
            LinearLayout scanCurrentLocation,
            LinearLayout scanNewLocation,
            LinearLayout commitButton,
            EditText currentLocation,
            EditText caseQty,
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
        if (commitButton != null && ( newLocation != null && !newLocation.getText().toString().isEmpty())) {
            commitButton.setVisibility(VISIBLE);
        }else if ( commitButton != null ){
            commitButton.setVisibility(GONE);
        }
        if (caseQty != null) {
            caseQty.setEnabled(false);
        }
        if (currentLocation != null) {
            currentLocation.setEnabled(false);
        }
        if (newLocation != null) {
            newLocation.setEnabled(false);
        }
    }

    public static void setEditMode(
            LinearLayout scanItem,
            LinearLayout scanCurrentLocation,
            LinearLayout scanNewLocation,
            LinearLayout commitButton,
            EditText caseQty,
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
        if (commitButton != null) {
            commitButton.setVisibility(GONE);
        }
        if (caseQty != null) {
            caseQty.setEnabled(true);
        }
        if (newLocation != null) {
            newLocation.setEnabled(true);
        }
    }

    public static void setItemViews(
            TextView inputItemSku,
            TextView inputItemDescription,
            TextView inputTagNumber,
            TextView inputPackSize,
            TextView inputReceivedDate,
            TextView inputExpirationDate,
            String sku,
            String description,
            String tagNumber,
            String packSize,
            String receivedDate,
            String expirationDate) {
        inputItemSku.setText(sku);
        inputItemDescription.setText(description);
        inputTagNumber.setText(tagNumber);
        inputPackSize.setText(packSize);
        inputReceivedDate.setText(receivedDate);
        inputExpirationDate.setText(expirationDate);
    }

}
