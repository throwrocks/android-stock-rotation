package rocks.athrow.android_stock_rotation.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.api.APIResponse;
import rocks.athrow.android_stock_rotation.data.Item;
import rocks.athrow.android_stock_rotation.data.Location;
import rocks.athrow.android_stock_rotation.data.RealmQueries;
import rocks.athrow.android_stock_rotation.data.Transaction;
import rocks.athrow.android_stock_rotation.util.Utilities;
import rocks.athrow.android_stock_rotation.zxing.IntentIntegrator;
import rocks.athrow.android_stock_rotation.zxing.IntentResult;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * TransactionBaseActivity
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

    public String mRotationType;
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
        if (mRotationType.equals(MainActivity.MODULE_MOVING) || mRotationType.equals(MainActivity.MODULE_RECEIVING)) {
            if (mButtonCommit != null && (mNewLocationView != null && !mNewLocationView.getText().toString().isEmpty())) {
                mButtonCommit.setVisibility(VISIBLE);
            } else if (mButtonCommit != null) {
                mButtonCommit.setVisibility(GONE);
            }
        } else {
            mButtonCommit.setVisibility(VISIBLE);
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

    public void baseSetCaseQtyView(String caseQty) {
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

    /**
     * onActivityResult
     *
     * @param requestCode the request code
     * @param resultCode  the result code
     * @param intent      the intent from the barcode initiateScan
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (mScanType == null) {
            Log.e("mScanType", "null");
            return;
        }
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult == null) {
            Log.e("scanResult", "null");
            return;
        }
        String contents = scanResult.getContents();
        if (contents == null) {
            Log.e("contents", "null");
            return;
        }
        mBarcodeContents = contents;
        Log.e("Barcode", contents);
        Context context = getApplicationContext();
        Resources res = getResources();
        int toastLenght = Toast.LENGTH_SHORT;
        switch (mScanType) {
            case SCAN_ITEM:
                RealmResults<Item> items = RealmQueries.getItemByTagNumber(context, contents);
                if (items.size() > 0) {
                    Item record = items.get(0);
                    mItemId = record.getId();
                    String sku = Integer.toString(record.getSKU());
                    String description = record.getDescription();
                    String tagNumber = record.getTagNumber();
                    String packSize = record.getPackSize();
                    String receivedDate = record.getReceivedDate();
                    String expirationDate = record.getExpirationDate();
                    mReceivingId = record.getReceivingId();
                    baseSetItemViews(
                            sku,
                            description,
                            tagNumber,
                            packSize,
                            receivedDate,
                            expirationDate
                    );
                    baseSaveTransaction();
                } else {
                    Utilities.showToast(context, res.getString(R.string.error_item_not_found), toastLenght);
                }
                break;
            case SCAN_CURRENT_LOCATION:
                RealmResults<Location> currentLocations = RealmQueries.getLocationByBarcode(context, contents);
                if (currentLocations.size() > 0) {
                    Location record = currentLocations.get(0);
                    String location = record.getLocation();
                    baseSetCurrentLocationView(location);
                    baseSaveTransaction();
                } else {
                    Utilities.showToast(context, res.getString(R.string.error_location_not_found), toastLenght);
                }
                break;
            case SCAN_NEW_LOCATION:
                RealmResults<Location> newLocations = RealmQueries.getLocationByBarcode(context, contents);
                if (newLocations.size() > 0) {
                    Location record = newLocations.get(0);
                    String location = record.getLocation();
                    baseSetNewLocationView(location);
                    baseSaveTransaction();
                } else {
                    Utilities.showToast(context, res.getString(R.string.error_location_not_found), toastLenght);
                }
                break;
        }
    }

    /**
     * baseSaveTransaction
     * A method to baseSaveTransaction the transaction record
     *
     * @return 1 for succes, 0 for error
     */
    public int baseSaveTransaction() {
        String skuString;
        String itemDescription;
        String tagNumber;
        String packSize;
        String receivedDate;
        String expirationDate;
        String caseQtyString;
        String currentLocation = null;
        String newLocation = null;
        skuString = mInputItemSku.getText().toString();
        itemDescription = mInputItemDescription.getText().toString();
        tagNumber = mInputItemSku.getText().toString();
        packSize = mInputPackSize.getText().toString();
        receivedDate = mInputReceivedDate.getText().toString();
        expirationDate = mInputExpirationDate.getText().toString();
        caseQtyString = mCaseQtyView.getText().toString();
        if (mCurrentLocationView != null && (mRotationType.equals(MainActivity.MODULE_MOVING) || mRotationType.equals(MainActivity.MODULE_STAGING))) {
            currentLocation = mCurrentLocationView.getText().toString();
        }
        if (mNewLocationView != null && ( mRotationType.equals(MainActivity.MODULE_MOVING) || mRotationType.equals(MainActivity.MODULE_RECEIVING))) {
            newLocation = mNewLocationView.getText().toString();
        }
        APIResponse apiResponse = RealmQueries.saveTransaction(
                getApplicationContext(),
                mRotationType,
                mTransactionId,
                mItemId,
                skuString,
                itemDescription,
                tagNumber,
                packSize,
                mReceivingId,
                receivedDate,
                expirationDate,
                caseQtyString,
                currentLocation,
                newLocation);
        if (apiResponse.getResponseCode() == 200) {
            return 1;
        } else {
            return 0;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scan, menu);
        if (mMode.equals(MODE_EDIT)) {
            menu.findItem(R.id.scan_edit).setVisible(false);
            menu.findItem(R.id.scan_save).setVisible(true);
        } else {
            menu.findItem(R.id.scan_edit).setVisible(true);
            menu.findItem(R.id.scan_save).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }


}
