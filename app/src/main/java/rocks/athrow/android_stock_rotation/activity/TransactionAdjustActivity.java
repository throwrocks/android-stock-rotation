package rocks.athrow.android_stock_rotation.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.Constants;
import rocks.athrow.android_stock_rotation.data.RealmQueries;
import rocks.athrow.android_stock_rotation.data.Transaction;
import rocks.athrow.android_stock_rotation.util.Utilities;

import static rocks.athrow.android_stock_rotation.data.Constants.BARCODE_CONTENTS;
import static rocks.athrow.android_stock_rotation.data.Constants.IN;
import static rocks.athrow.android_stock_rotation.data.Constants.ITEM_ID;
import static rocks.athrow.android_stock_rotation.data.Constants.MODE;
import static rocks.athrow.android_stock_rotation.data.Constants.MODE_EDIT;
import static rocks.athrow.android_stock_rotation.data.Constants.MODE_VIEW;
import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_ADJUST;
import static rocks.athrow.android_stock_rotation.data.Constants.NAME;
import static rocks.athrow.android_stock_rotation.data.Constants.OUT;
import static rocks.athrow.android_stock_rotation.data.Constants.SCAN_CURRENT_LOCATION;
import static rocks.athrow.android_stock_rotation.data.Constants.SCAN_ITEM;
import static rocks.athrow.android_stock_rotation.data.Constants.SCAN_TYPE;
import static rocks.athrow.android_stock_rotation.data.Constants.TRANSACTION_ID;

public class TransactionAdjustActivity extends TransactionBaseActivity {
    private String mCurrentLocation;
    private String mTagNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_adjust);
        Intent intent = getIntent();
        if (intent != null) {
            mRotationType = intent.getStringExtra(Constants.MODULE_TYPE);
            mTransactionId = intent.getStringExtra(TRANSACTION_ID);
            mItemId = intent.getStringExtra(ITEM_ID);
            mMode = intent.getStringExtra(MODE);
            mCurrentLocation = intent.getStringExtra(Constants.CURRENT_LOCATION);
            mTagNumber = intent.getStringExtra(Constants.TAG_NUMBER);
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mInputItemSku = (TextView) findViewById(R.id.input_item_sku);
        mInputItemDescription = (TextView) findViewById(R.id.input_item_description);
        mInputTagNumber = (TextView) findViewById(R.id.input_tag_number);
        mInputPackSize = (TextView) findViewById(R.id.input_pack_size);
        mInputReceivedDate = (TextView) findViewById(R.id.input_received_date);
        mInputExpirationDate = (TextView) findViewById(R.id.input_expiration_date);
        mButtonScanItem = (LinearLayout) findViewById(R.id.scan_item);
        mButtonScanCurrentLocation = (LinearLayout) findViewById(R.id.scan_current_location);
        mCurrentLocationView = (EditText) findViewById(R.id.input_current_location);
        mCaseQtyView = (EditText) findViewById(R.id.input_case_qty);
        mButtonCommit = (LinearLayout) findViewById(R.id.button_adjust);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(MODULE_ADJUST);
        }
        setCurrentMode();
        if (mCurrentLocation != null && mTagNumber != null) {
            scanItem(mTagNumber);
            scanCurrentLocation(mCurrentLocation, NAME);
        }
    }

    /**
     * setCurrentMode
     * A method to set the views according to the mode (view vs edit)
     */
    private void setCurrentMode() {
        if (mMode.equals(MODE_EDIT)) {
            setEditMode();
        } else {
            setViewMode();
        }
    }

    /**
     * baseSetEditMode
     * A method to set the layout on edit mode
     * Edit mode allows changing the transaction fields
     */
    private void setEditMode() {
        setTransactionViews();
        baseSetEditMode();
        // Set the click listeners for the initiateScan buttons
        mButtonScanItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScanType = SCAN_ITEM;
                initiateScan();
            }
        });
        mButtonScanCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScanType = SCAN_CURRENT_LOCATION;
                initiateScan();
            }
        });
    }

    /**
     * setViewMode
     * A method to set the layout on view mode
     * View mode does not allow editing the transaction fields
     */
    private void setViewMode() {
        setTransactionViews();
        baseSetViewMode();
        // Set the click listeners on the commit button (Move)
        mButtonCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustButton();
            }
        });

    }

    /**
     * setTransactionViews
     * A method to set the views with transaction date
     * 1. Set the current location
     * 2. Set the new location
     * 3. Set the case qty
     * 4. Set the item views (sku, item desc., etc)
     */
    private void setTransactionViews() {
        Transaction transaction = RealmQueries.getTransaction(getApplicationContext(), mTransactionId);
        if (transaction != null) {
            baseSetItemViews(
                    transaction.getSkuString(),
                    transaction.getItemDescription(),
                    transaction.getTagNumber(),
                    transaction.getPackSize(),
                    transaction.getReceivedDate(),
                    transaction.getExpirationDate()
            );
            baseSetCurrentLocationView(transaction.getLocationStart());
            baseSetCaseQtyView(transaction.getQtyCasesString());
        }
    }

    private void adjustButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Commit adjustment?")
                .setTitle("Adjust Item");
        builder.setPositiveButton("Commit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if ( commitTransaction() ){
                    Utilities.showToast(getApplicationContext(), "Adjustment Completed!", Toast.LENGTH_SHORT);
                    finish();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * commitTransaction
     * A method to commit the adjust transaction
     */
    private boolean commitTransaction() {
        Context context = getApplicationContext();
        Transaction transaction = RealmQueries.getTransaction(context, mTransactionId);
        if (transaction != null && transaction.getIsValidRecord()) {
            int newCaseQty = transaction.getQtyCases();
            int remainingQty = RealmQueries.getCountCasesByLocation(context, mCurrentLocation, mItemId).intValue();
            if ( newCaseQty == remainingQty ){
                Utilities.showToast(getApplicationContext(), "The new quantity and the existing quantity are the same.", Toast.LENGTH_SHORT);
                return false;
            } else if (newCaseQty > remainingQty) {
                RealmQueries.saveTransfer(
                        context,
                        transaction.getId(),
                        transaction.getType1(),
                        IN,
                        transaction.getItemId(),
                        transaction.getSku(),
                        transaction.getItemDescription(),
                        transaction.getTagNumber(),
                        transaction.getPackSize(),
                        transaction.getReceivingId(),
                        transaction.getReceivedDate(),
                        transaction.getExpirationDate(),
                        transaction.getLocationStart(),
                        ( newCaseQty - remainingQty )
                );
            }
            else if ( newCaseQty < remainingQty ){
                RealmQueries.saveTransfer(context,
                        transaction.getId(),
                        transaction.getType1(),
                        OUT,
                        transaction.getItemId(),
                        transaction.getSku(),
                        transaction.getItemDescription(),
                        transaction.getTagNumber(),
                        transaction.getPackSize(),
                        transaction.getReceivingId(),
                        transaction.getReceivedDate(),
                        transaction.getExpirationDate(),
                        transaction.getLocationStart(),
                        ( remainingQty - newCaseQty )
                );
            }

            RealmQueries.commitTransaction(context, mTransactionId);
            return true;
        }else{
            return false;
        }

    }

    private boolean isQtyEmpty() {
        String caseQty = mCaseQtyView.getText().toString();
        return caseQty.isEmpty();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan_delete:
                baseDeleteTransaction(mTransactionId);
                return super.onOptionsItemSelected(item);
            case R.id.scan_save:
                if (isQtyEmpty()) {
                    Utilities.showToast(getApplicationContext(),
                            "Please enter a case quantity.",
                            Toast.LENGTH_SHORT);
                } else {
                    int save = baseSaveTransaction();
                    if (save == 1) {
                        Transaction transaction = RealmQueries.getTransaction(getApplicationContext(), mTransactionId);
                        if (transaction != null && transaction.getIsValidRecord()) {
                            setViewMode();
                            mMode = MODE_VIEW;
                            invalidateOptionsMenu();
                        } else {
                            Utilities.showToast(getApplicationContext(),
                                    "Invalid record. The item, the current location, and the quantity are required.",
                                    Toast.LENGTH_SHORT);
                        }
                    }
                }
                return super.onOptionsItemSelected(item);
            case R.id.scan_edit:
                setEditMode();
                mMode = MODE_EDIT;
                invalidateOptionsMenu();
                return super.onOptionsItemSelected(item);
            case android.R.id.home:
                baseBackPressed();
                return (true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mScanType = savedInstanceState.getString(SCAN_TYPE);
        mBarcodeContents = savedInstanceState.getString(BARCODE_CONTENTS);
        mTransactionId = savedInstanceState.getString(TRANSACTION_ID);
        mItemId = savedInstanceState.getString(ITEM_ID);
        mMode = savedInstanceState.getString(MODE);
        setCurrentMode();
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        if (!isScanning && isQtyEmpty()) {
            baseDeleteTransaction(mTransactionId);
        }
        super.onPause();
    }
}
