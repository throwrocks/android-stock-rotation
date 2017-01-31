package rocks.athrow.android_stock_rotation.activity;

import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.api.APIResponse;
import rocks.athrow.android_stock_rotation.data.RealmQueries;
import rocks.athrow.android_stock_rotation.data.Item;
import rocks.athrow.android_stock_rotation.data.Location;
import rocks.athrow.android_stock_rotation.data.Transaction;
import rocks.athrow.android_stock_rotation.util.Utilities;
import rocks.athrow.android_stock_rotation.zxing.IntentIntegrator;
import rocks.athrow.android_stock_rotation.zxing.IntentResult;

/**
 * TransactionMoveActivity
 * Created by joselopez on 1/13/17.
 */

public class TransactionMoveActivity extends TransactionBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_move);
        Intent intent = getIntent();
        if (intent != null) {
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
        mButtonScanItem = (LinearLayout) findViewById(R.id.scan_item);
        mButtonScanCurrentLocation = (LinearLayout) findViewById(R.id.scan_current_location);
        mButtonScanNewLocation = (LinearLayout) findViewById(R.id.scan_new_location);
        mCurrentLocationView = (EditText) findViewById(R.id.input_current_location);
        mCaseQtyView = (EditText) findViewById(R.id.input_case_qty);
        mNewLocationView = (EditText) findViewById(R.id.input_new_location);
        mButtonCommit = (LinearLayout) findViewById(R.id.button_move);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(MainActivity.MODULE_MOVING);
        }
        setViews();
    }

    /**
     * setViews
     * A method to set the views according to the mode (view vs edit)
     */
    private void setViews() {
        if (mMode.equals(MODE_EDIT)) {
            setEditMode();
        } else {
            setViewMode();
        }
    }

    /**
     * setEditMode
     * A method to set the layout on edit mode
     */
    private void setEditMode() {
        setViewData();
        setEditMode(
                mButtonScanItem,
                mButtonScanCurrentLocation,
                mButtonScanNewLocation,
                mButtonCommit,
                mCaseQtyView,
                mNewLocationView);
        mButtonScanItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScanType = SCAN_ITEM;
                scan();
            }
        });
        mButtonScanCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScanType = SCAN_CURRENT_LOCATION;
                scan();
            }
        });
        mButtonScanNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScanType = SCAN_NEW_LOCATION;
                scan();
            }
        });

    }

    /**
     * setViewMode
     * A method to set the layout on view mode
     */
    private void setViewMode() {
        setViewData();
        setViewMode(
                mButtonScanItem,
                mButtonScanCurrentLocation,
                mButtonScanNewLocation,
                mButtonCommit,
                mCurrentLocationView,
                mCaseQtyView,
                mNewLocationView);
        mButtonCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveButton();
            }
        });

    }

    /**
     * setViewData
     * A method to set the views with transaction date
     */
    private void setViewData() {
        Transaction transaction = RealmQueries.getTransaction(getApplicationContext(), mTransactionId);
        if (transaction != null) {
            setNewLocationView(transaction.getLocationEnd());
            setCurrentLocationView(transaction.getLocationStart());
            setQtyViews(transaction.getQtyCasesString(), transaction.getQtyLooseString());
            setItemViews(
                    mInputItemSku,
                    mInputItemDescription,
                    mInputTagNumber,
                    mInputPackSize,
                    mInputReceivedDate,
                    mInputExpirationDate,
                    transaction.getSkuString(),
                    transaction.getItemDescription(),
                    transaction.getTagNumber(),
                    transaction.getPackSize(),
                    transaction.getReceivedDate(),
                    transaction.getExpirationDate()
            );
        }
    }

    /**
     * scan
     * A method to initiate the barcode scanning
     */
    private void scan() {
        if (mScanType != null) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan();
        }
    }

    /**
     * save
     * A method to save the transaction record
     *
     * @return 1 for succes, 0 for error
     */
    private int save() {
        String skuString = mInputItemSku.getText().toString();
        String itemDescription = mInputItemDescription.getText().toString();
        String tagNumber = mInputTagNumber.getText().toString();
        String packSize = mInputPackSize.getText().toString();
        String receivedDate = mInputReceivedDate.getText().toString();
        String expirationDate = mInputExpirationDate.getText().toString();
        String caseQtyString = mCaseQtyView.getText().toString();
        String currentLocation = mCurrentLocationView.getText().toString();
        String newLocation = mNewLocationView.getText().toString();
        APIResponse apiResponse = RealmQueries.saveTransaction(
                getApplicationContext(),
                MainActivity.MODULE_MOVING,
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

    private void moveButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Complete the move of this item?")
                .setTitle("Move Item");
        builder.setPositiveButton("Commit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                commitTransaction();
                Utilities.showToast(getApplicationContext(), "Move Completed!", Toast.LENGTH_SHORT);
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
        if (transaction != null && transaction.isValidRecord()) {
            RealmQueries.saveTransfer(
                    context,
                    transaction.getId(),
                    transaction.getType1(),
                    OUT,
                    transaction.getItemId(),
                    transaction.getSku(),
                    transaction.getItemDescription(),
                    transaction.getPackSize(),
                    transaction.getReceivingId(),
                    transaction.getReceivedDate(),
                    transaction.getLocationStart(),
                    transaction.getQtyCases()
                    //transaction.getQtyLoose()
            );
            RealmQueries.saveTransfer(
                    context,
                    transaction.getId(),
                    transaction.getType1(),
                    IN,
                    transaction.getItemId(),
                    transaction.getSku(),
                    transaction.getItemDescription(),
                    transaction.getPackSize(),
                    transaction.getReceivingId(),
                    transaction.getReceivedDate(),
                    transaction.getLocationEnd(),
                    transaction.getQtyCases()
                    //transaction.getQtyLoose()
            );
            RealmQueries.commitTransaction(context, mTransactionId);
        }
    }

    /**
     * deleteTransaction
     * A caller method to delete a transaction
     *
     * @param transactionId the transaction id
     */
    private void deleteTransaction(String transactionId) {
        APIResponse apiResponse = RealmQueries.deleteTransaction(getApplicationContext(), transactionId);
        Utilities.showToast(getApplicationContext(), apiResponse.getResponseText(), Toast.LENGTH_SHORT);
        if (apiResponse.getResponseCode() == 200) {
            finish();
        }
    }


    /**
     * setQtys
     * A method to set the item qtys
     *
     * @param caseQty  the case qty
     * @param looseQty the loose qty
     */
    private void setQtyViews(String caseQty, String looseQty) {
        TextView inputCaseQty = (TextView) findViewById(R.id.input_case_qty);
        //TextView inputLooseQty = (TextView) findViewById(R.id.input_loose_qty);
        Utilities.setQtys(inputCaseQty, /*inputLooseQty*/ caseQty /*looseQty*/);
    }

    /**
     * setCurrentLocationView
     *
     * @param location the location name
     */
    private void setCurrentLocationView(String location) {
        TextView inputCurrentLocation = (TextView) findViewById(R.id.input_current_location);
        Utilities.setCurrentLocationView(inputCurrentLocation, location);
    }

    /**
     * setNewLocationView
     *
     * @param location the location name
     */
    private void setNewLocationView(String location) {
        EditText inputNewLocation = (EditText) findViewById(R.id.input_new_location);
        Utilities.setCurrentLocationView(inputNewLocation, location);
    }

    /**
     * onActivityResult
     *
     * @param requestCode the request code
     * @param resultCode  the result code
     * @param intent      the intent from the barcode scan
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
                    setItemViews(
                            mInputItemSku,
                            mInputItemDescription,
                            mInputTagNumber,
                            mInputPackSize,
                            mInputReceivedDate,
                            mInputExpirationDate,
                            sku,
                            description,
                            tagNumber,
                            packSize,
                            receivedDate,
                            expirationDate
                    );
                    save();
                } else {
                    Utilities.showToast(context, res.getString(R.string.error_item_not_found), toastLenght);
                }
                break;
            case SCAN_CURRENT_LOCATION:
                RealmResults<Location> currentLocations = RealmQueries.getLocation(context, contents);
                if (currentLocations.size() > 0) {
                    Location record = currentLocations.get(0);
                    String location = record.getLocation();
                    setCurrentLocationView(location);
                    save();
                } else {
                    Utilities.showToast(context, res.getString(R.string.error_location_not_found), toastLenght);
                }
                break;
            case SCAN_NEW_LOCATION:
                RealmResults<Location> newLocations = RealmQueries.getLocation(context, contents);
                if (newLocations.size() > 0) {
                    Location record = newLocations.get(0);
                    String location = record.getLocation();
                    setNewLocationView(location);
                    save();
                } else {
                    Utilities.showToast(context, res.getString(R.string.error_location_not_found), toastLenght);
                }
                break;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan_delete:
                deleteTransaction(mTransactionId);
                return super.onOptionsItemSelected(item);
            case R.id.scan_save:
                int save = save();
                if (save == 1) {
                    Transaction transaction = RealmQueries.getTransaction(getApplicationContext(), mTransactionId);
                    if (transaction != null && transaction.isValidRecord()) {
                        setViewMode();
                        mMode = MODE_VIEW;
                        invalidateOptionsMenu();
                    } else {
                        Utilities.showToast(getApplicationContext(),
                                "Invalid record. The item, the current location, and the quantity are required.",
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
                back();
                return (true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        back();
        super.onBackPressed();
    }

    /**
     * back
     * A method to handle the up navigation and back button press
     * It deletes the transaction record if it's invalid
     */
    private void back() {
        Transaction transaction = RealmQueries.getTransaction(getApplicationContext(), mTransactionId);
        if (transaction != null && !transaction.isValidRecord()) {
            RealmQueries.deleteTransaction(getApplicationContext(), mTransactionId);
        }
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("barcode_contents", mBarcodeContents);
        outState.putString("scan_type", mScanType);
        outState.putString(TRANSACTION_ID, mTransactionId);
        outState.putString(ITEM_ID, mItemId);
        outState.putString(MODE, mMode);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mScanType = savedInstanceState.getString("scan_type");
        mBarcodeContents = savedInstanceState.getString("barcode_contents");
        mTransactionId = savedInstanceState.getString(TRANSACTION_ID);
        mItemId = savedInstanceState.getString(ITEM_ID);
        mMode = savedInstanceState.getString(MODE);
        setViews();
        super.onRestoreInstanceState(savedInstanceState);
    }


}
