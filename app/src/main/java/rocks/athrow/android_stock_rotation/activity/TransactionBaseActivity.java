package rocks.athrow.android_stock_rotation.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import static rocks.athrow.android_stock_rotation.data.Constants.BARCODE;
import static rocks.athrow.android_stock_rotation.data.Constants.BARCODE_CONTENTS;
import static rocks.athrow.android_stock_rotation.data.Constants.ITEM_ID;
import static rocks.athrow.android_stock_rotation.data.Constants.MODE;
import static rocks.athrow.android_stock_rotation.data.Constants.MODE_EDIT;
import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_ADJUST;
import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_MOVING;
import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_RECEIVING;
import static rocks.athrow.android_stock_rotation.data.Constants.NAME;
import static rocks.athrow.android_stock_rotation.data.Constants.SCAN_CURRENT_LOCATION;
import static rocks.athrow.android_stock_rotation.data.Constants.SCAN_ITEM;
import static rocks.athrow.android_stock_rotation.data.Constants.SCAN_NEW_LOCATION;
import static rocks.athrow.android_stock_rotation.data.Constants.SCAN_TYPE;
import static rocks.athrow.android_stock_rotation.data.Constants.TRANSACTION_ID;

/**
 * TransactionBaseActivity
 * Created by joselopez on 1/31/17.
 */

public abstract class TransactionBaseActivity extends AppCompatActivity {
    String mRotationType;
    TextView mInputItemSku;
    TextView mInputItemDescription;
    TextView mInputTagNumber;
    TextView mInputPackSize;
    TextView mInputReceivedDate;
    TextView mInputExpirationDate;
    String mBarcodeContents;
    String mScanType;
    String mMode;
    String mTransactionId;
    String mItemId;
    String mTagNumber;
    private int mReceivingId;
    LinearLayout mButtonScanItem;
    LinearLayout mButtonScanCurrentLocation;
    LinearLayout mButtonScanNewLocation;
    LinearLayout mButtonSetPrimaryLocation;
    EditText mCurrentLocationView;
    EditText mCaseQtyView;
    EditText mNewLocationView;
    LinearLayout mButtonCommit;
    boolean isScanning;

    /**
     * baseSetViewMode
     * Sets the layout to view mode (no editing)
     */
    void baseSetViewMode() {
        if (mButtonScanItem != null) {
            mButtonScanItem.setVisibility(GONE);
        }
        if (mButtonScanCurrentLocation != null) {
            mButtonScanCurrentLocation.setVisibility(GONE);
        }
        if (mButtonScanNewLocation != null) {
            mButtonScanNewLocation.setVisibility(GONE);
        }
        if (mButtonSetPrimaryLocation!=null){
            mButtonSetPrimaryLocation.setVisibility(GONE);
        }
        if (mRotationType.equals(MODULE_MOVING) || mRotationType.equals(MODULE_RECEIVING)) {
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
    }

    /**
     * baseSetEditMode
     * Sets the layout to edit mode
     */
    void baseSetEditMode() {
        if (mButtonScanItem != null) {
            mButtonScanItem.setVisibility(VISIBLE);
        }
        if (mButtonScanCurrentLocation != null) {
            mButtonScanCurrentLocation.setVisibility(VISIBLE);
        }
        if (mButtonScanNewLocation != null) {
            mButtonScanNewLocation.setVisibility(VISIBLE);
        }
        if (mButtonSetPrimaryLocation!=null){
            mButtonSetPrimaryLocation.setVisibility(VISIBLE);
        }
        if (mButtonCommit != null) {
            mButtonCommit.setVisibility(GONE);
        }
        if (mCaseQtyView != null) {
            mCaseQtyView.setEnabled(true);
        }
    }

    /**
     * Sets the item views
     *
     * @param sku            the item's sku
     * @param description    the item's description
     * @param tagNumber      the item's tag number
     * @param packSize       the item's pack size
     * @param receivedDate   the item's received date
     * @param expirationDate the item's expiration date
     */
    void baseSetItemViews(
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

    /**
     * baseSetCaseQtyView
     *
     * @param caseQty sets the transaction's case qty
     */
    void baseSetCaseQtyView(String caseQty) {
        mCaseQtyView.setText(caseQty);
    }

    /**
     * baseSetCurrentLocationView
     *
     * @param location the transaction's current location (for move and stage)
     */
    void baseSetCurrentLocationView(String location, boolean isPrimary) {
        mCurrentLocationView.setText(location);
        if ( isPrimary ){
            mCurrentLocationView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primaryLocation));
        }else{
            mCurrentLocationView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primaryText));;
        }
    }

    /**
     * baseSetNewLocationView
     *
     * @param location the transaction's new location (for receive and move)
     */
    void baseSetNewLocationView(String location, boolean isPrimary) {
        mNewLocationView.setText(location);
        if ( isPrimary ){
            mNewLocationView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primaryLocation));
        }else{
            mNewLocationView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primaryText));;
        }
    }


    /**
     * initiateScan
     * Initiates the barcode scanner app
     */
    void initiateScan() {
        if (mScanType != null) {
            isScanning = true;
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan();
        }
    }

    /**
     * baseDeleteTransaction
     * Deletes the transaction record. Called from the options menu
     *
     * @param transactionId the id of the transaction to be deleted
     */
    void baseDeleteTransaction(String transactionId) {
        APIResponse apiResponse = RealmQueries.deleteTransaction(getApplicationContext(), transactionId);
        Utilities.showToast(getApplicationContext(), apiResponse.getResponseText(), Toast.LENGTH_SHORT);
        if (apiResponse.getResponseCode() == 200) {
            finish();
        }
    }

    /**
     * onBackPressed
     */
    @Override
    public void onBackPressed() {
        baseBackPressed();
        super.onBackPressed();
    }

    /**
     * baseBackPressed
     * Handle the up navigation and back button press
     * Deletes the transaction if the record is invalid
     */
    void baseBackPressed() {
        Transaction transaction = RealmQueries.getTransaction(getApplicationContext(), mTransactionId);
        if (transaction != null && !transaction.getIsValidRecord()) {
            RealmQueries.deleteTransaction(getApplicationContext(), mTransactionId);
        }
        finish();
    }

    /**
     * onActivityResult
     * Called after scanning a barcode
     * Handles scanning an item or a location (current location or new location)
     *
     * @param requestCode the request code
     * @param resultCode  the result code
     * @param intent      the intent from the barcode initiateScan
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        isScanning = false;
        if (mScanType == null) {
            return;
        }
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult == null) {
            return;
        }
        String contents = scanResult.getContents();
        if (contents == null) {
            return;
        }
        mBarcodeContents = contents;
        switch (mScanType) {
            case SCAN_ITEM:
                scanItem(contents);
                break;
            case SCAN_CURRENT_LOCATION:
                scanCurrentLocation(contents, BARCODE);
                break;
            case SCAN_NEW_LOCATION:
                scanNewLocation(contents);
                break;
        }
    }

    void scanItem(String contents) {
        Context context = getApplicationContext();
        Resources res = getResources();
        int toastLength = Toast.LENGTH_SHORT;
        RealmResults<Item> items = RealmQueries.getItemByTagNumber(context, contents);
        if (items.size() > 0) {
            Item record = items.get(0);
            mItemId = record.getId();
            mTagNumber = record.getTagNumber();
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
            Utilities.showToast(context, res.getString(R.string.error_item_not_found), toastLength);
        }
    }

    /**
     * scanCurrentLocation
     *
     * @param contents the scan or intent contents
     * @param type     barcode number or location name
     */
    void scanCurrentLocation(String contents, String type) {
        if (!type.equals(BARCODE) && !type.equals(NAME)) {
            return;
        }
        Context context = getApplicationContext();
        Resources res = getResources();
        int toastLength = Toast.LENGTH_SHORT;
        RealmResults<Location> currentLocations = null;
        if (type.equals(BARCODE)) {
            currentLocations = RealmQueries.getLocationByBarcode(context, contents);
        } else if (type.equals(NAME)) {
            currentLocations = RealmQueries.getLocationByName(context, contents);
        }
        if (currentLocations != null && currentLocations.size() > 0) {
            Location record = currentLocations.get(0);
            String location = record.getLocation();
            boolean isPrimary = record.isPrimary();
            baseSetCurrentLocationView(location, isPrimary);
            baseSaveTransaction();
        } else {
            Utilities.showToast(context, res.getString(R.string.error_location_not_found), toastLength);
        }
    }

    private void scanNewLocation(String contents) {
        Context context = getApplicationContext();
        Resources res = getResources();
        int toastLength = Toast.LENGTH_SHORT;
        RealmResults<Location> newLocations = RealmQueries.getLocationByBarcode(context, contents);
        if (newLocations.size() > 0) {
            Location record = newLocations.get(0);
            String location = record.getLocation();
            boolean isPrimary = record.isPrimary();
            baseSetNewLocationView(location, isPrimary);
            baseSaveTransaction();
        } else {
            Utilities.showToast(context, res.getString(R.string.error_location_not_found), toastLength);
        }
    }

    void setPrimaryLocation(String location){
        baseSetNewLocationView(location, true);
        baseSaveTransaction();
    }

    /**
     * baseSaveTransaction
     * Save the transaction record
     *
     * @return 1 for success, 0 for error
     */
    int baseSaveTransaction() {
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
        tagNumber = mInputTagNumber.getText().toString();
        packSize = mInputPackSize.getText().toString();
        receivedDate = mInputReceivedDate.getText().toString();
        expirationDate = mInputExpirationDate.getText().toString();
        caseQtyString = mCaseQtyView.getText().toString();
        if (mCurrentLocationView != null && (mRotationType.equals(MODULE_MOVING) || mRotationType.equals(MODULE_ADJUST) /* || mRotationType.equals(MODULE_STAGING)*/)) {
            currentLocation = mCurrentLocationView.getText().toString();
        }
        if (mNewLocationView != null && (mRotationType.equals(MODULE_MOVING) || mRotationType.equals(MODULE_RECEIVING))) {
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

    /**
     * onCreateOptionsMenu
     *
     * @param menu the menu view
     * @return the inflated menu
     */
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(BARCODE_CONTENTS, mBarcodeContents);
        outState.putString(SCAN_TYPE, mScanType);
        outState.putString(TRANSACTION_ID, mTransactionId);
        outState.putString(ITEM_ID, mItemId);
        outState.putString(MODE, mMode);
        super.onSaveInstanceState(outState);
    }

}
