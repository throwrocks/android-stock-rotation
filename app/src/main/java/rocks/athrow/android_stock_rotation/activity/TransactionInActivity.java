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
import rocks.athrow.android_stock_rotation.data.RealmQueries;
import rocks.athrow.android_stock_rotation.data.Transaction;
import rocks.athrow.android_stock_rotation.util.Utilities;

import static rocks.athrow.android_stock_rotation.data.Constants.BARCODE_CONTENTS;
import static rocks.athrow.android_stock_rotation.data.Constants.IN;
import static rocks.athrow.android_stock_rotation.data.Constants.ITEM_ID;
import static rocks.athrow.android_stock_rotation.data.Constants.MODE;
import static rocks.athrow.android_stock_rotation.data.Constants.MODE_EDIT;
import static rocks.athrow.android_stock_rotation.data.Constants.MODE_VIEW;
import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_RECEIVING;
import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_TYPE;
import static rocks.athrow.android_stock_rotation.data.Constants.SCAN_ITEM;
import static rocks.athrow.android_stock_rotation.data.Constants.SCAN_NEW_LOCATION;
import static rocks.athrow.android_stock_rotation.data.Constants.SCAN_TYPE;
import static rocks.athrow.android_stock_rotation.data.Constants.TRANSACTION_ID;

/**
 * TransactionInActivity
 * Created by joselopez on 1/13/17.
 */

public class TransactionInActivity extends TransactionBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_in);
        Intent intent = getIntent();
        if (intent != null) {
            mRotationType = intent.getStringExtra(MODULE_TYPE);
            mTransactionId = intent.getStringExtra(TRANSACTION_ID);
            mItemId = intent.getStringExtra(ITEM_ID);
            mMode = intent.getStringExtra(MODE);
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mInputItemSku = (TextView) findViewById(R.id.input_item_sku);
        mInputItemDescription = (TextView) findViewById(R.id.input_item_description);
        mInputTagNumber = (TextView) findViewById(R.id.input_tag_number);
        mInputPackSize = (TextView) findViewById(R.id.input_pack_size);
        mInputReceivedDate = (TextView) findViewById(R.id.input_received_date);
        mInputExpirationDate = (TextView) findViewById(R.id.input_expiration_date);
        mNewLocationView = (EditText) findViewById(R.id.input_new_location);
        mButtonScanItem = (LinearLayout) findViewById(R.id.scan_item);
        mButtonScanNewLocation = (LinearLayout) findViewById(R.id.scan_new_location);
        mCaseQtyView = (EditText) findViewById(R.id.input_case_qty);
        mButtonCommit = (LinearLayout) findViewById(R.id.button_receive);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(MODULE_RECEIVING);
        }
        setCurrentMode();
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
        mButtonScanNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScanType = SCAN_NEW_LOCATION;
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
                receiveButton();
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
            baseSetNewLocationView(transaction.getLocationEnd());
            baseSetCaseQtyView(transaction.getQtyCasesString());
        }
    }

    private void receiveButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Commit receiving?")
                .setTitle("Receive Item");
        builder.setPositiveButton("Commit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                commitTransaction();
                Utilities.showToast(getApplicationContext(), "Receiving Completed!", Toast.LENGTH_SHORT);
                finish();
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
     * A method to commit the move transaction
     */
    private void commitTransaction() {
        Context context = getApplicationContext();
        Transaction transaction = RealmQueries.getTransaction(context, mTransactionId);
        if (transaction != null && transaction.getIsValidRecord()) {
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
                    transaction.getLocationEnd(),
                    transaction.getQtyCases()
            );
            RealmQueries.commitTransaction(context, mTransactionId);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan_delete:
                baseDeleteTransaction(mTransactionId);
                return super.onOptionsItemSelected(item);
            case R.id.scan_save:
                int save = baseSaveTransaction();
                if (save == 1) {
                    Transaction transaction = RealmQueries.getTransaction(getApplicationContext(), mTransactionId);
                    if (transaction != null && transaction.getIsValidRecord()) {
                        setViewMode();
                        mMode = MODE_VIEW;
                        invalidateOptionsMenu();
                    } else {
                        Utilities.showToast(getApplicationContext(),
                                "Invalid record. The item, the new location, and the quantity are required.",
                                Toast.LENGTH_SHORT);
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

}
