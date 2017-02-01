package rocks.athrow.android_stock_rotation.activity;

import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import rocks.athrow.android_stock_rotation.api.APIResponse;
import rocks.athrow.android_stock_rotation.data.RealmQueries;
import rocks.athrow.android_stock_rotation.data.Transaction;
import rocks.athrow.android_stock_rotation.util.Utilities;
import rocks.athrow.android_stock_rotation.zxing.IntentIntegrator;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by joselopez on 1/31/17.
 */

public abstract class TransactionBaseActivity extends AppCompatActivity {
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


    public void baseSetViewMode() {
        if (mButtonScanItem != null) {
            mButtonScanItem.setVisibility(GONE);
        }
        if (mButtonScanCurrentLocation != null) {
            mButtonScanCurrentLocation.setVisibility(GONE);
        }
        if (mButtonScanNewLocation != null) {
            mButtonScanNewLocation.setVisibility(GONE);
        }
        if (mButtonCommit != null && (mNewLocationView != null && !mNewLocationView.getText().toString().isEmpty())) {
            mButtonCommit.setVisibility(VISIBLE);
        } else if (mButtonCommit != null) {
            mButtonCommit.setVisibility(GONE);
        }
        if (mCaseQtyView != null) {
            mCaseQtyView.setEnabled(false);
        }
        if (mCurrentLocationView != null) {
            mCurrentLocationView.setEnabled(false);
        }
        if (mNewLocationView != null) {
            mNewLocationView.setEnabled(false);
        }
    }

    public void baseSetEditMode() {
        if (mButtonScanItem != null) {
            mButtonScanItem.setVisibility(VISIBLE);
        }
        if (mButtonScanCurrentLocation != null) {
            mButtonScanCurrentLocation.setVisibility(VISIBLE);
        }
        if (mButtonScanNewLocation != null) {
            mButtonScanNewLocation.setVisibility(VISIBLE);
        }
        if (mButtonCommit != null) {
            mButtonCommit.setVisibility(GONE);
        }
        if (mCaseQtyView != null) {
            mCaseQtyView.setEnabled(true);
        }
        if (mNewLocationView != null) {
            mNewLocationView.setEnabled(true);
        }
    }

    public void baseSetItemViews(
            String sku,
            String description,
            String tagNumber,
            String packSize,
            String receivedDate,
            String expirationDate) {
        mInputItemSku.setText(sku);
        mInputItemDescription.setText(description);
        mInputTagNumber.setText(tagNumber);
        mInputPackSize.setText(packSize);
        mInputReceivedDate.setText(receivedDate);
        mInputExpirationDate.setText(expirationDate);
    }

    public void baseSetCaseQtyView(String caseQty){
        mCaseQtyView.setText(caseQty);
    }

    public void baseSetCurrentLocationView(String location) {
        mCurrentLocationView.setText(location);
    }

    public void baseSetNewLocationView(String location) {
        mNewLocationView.setText(location);
    }


    public void initiateScan() {
        if (mScanType != null) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan();
        }
    }

    public void baseDeleteTransaction(String transactionId) {
        APIResponse apiResponse = RealmQueries.deleteTransaction(getApplicationContext(), transactionId);
        Utilities.showToast(getApplicationContext(), apiResponse.getResponseText(), Toast.LENGTH_SHORT);
        if (apiResponse.getResponseCode() == 200) {
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        baseBackPressed();
        super.onBackPressed();
    }

    /**
     * baseBackPressed
     * A method to handle the up navigation and baseBackPressed button press
     * It deletes the transaction record if it's invalid
     */
    public void baseBackPressed() {
        Transaction transaction = RealmQueries.getTransaction(getApplicationContext(), mTransactionId);
        if (transaction != null && !transaction.isValidRecord()) {
            RealmQueries.deleteTransaction(getApplicationContext(), mTransactionId);
        }
        finish();
    }
}
