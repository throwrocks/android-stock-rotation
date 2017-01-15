package rocks.athrow.android_stock_rotation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import rocks.athrow.android_stock_rotation.data.DataUtilities;
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

public class TransactionMoveActivity extends AppCompatActivity {
    private static final String ITEM_ID = "item_id";
    private static final String TRANSACTION_ID = "transaction_id";
    private static final String MODE = "mode";
    private static final String MODE_EDIT = "edit";
    private static final String MODE_VIEW = "view";
    private static final String SCAN_ITEM = "item";
    private static final String SCAN_CURRENT_LOCATION = "currentLocation";
    private static final String SCAN_NEW_LOCATION = "newLocation";
    private String mScanType;
    private String mMode;
    private String mTransactionId;
    private String mItemId;
    LinearLayout mButtonScanItem;
    LinearLayout mButtonScanCurrentLocation;
    LinearLayout mButtonScanNewLocation;
    EditText mCurrentLocationView;
    EditText mCaseQtyView;
    EditText mLooseQtyView;
    EditText mNewLocationView;

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
        mButtonScanItem = (LinearLayout) findViewById(R.id.scan_item);
        mButtonScanCurrentLocation = (LinearLayout) findViewById(R.id.scan_current_location);
        mButtonScanNewLocation = (LinearLayout) findViewById(R.id.scan_new_location);
        mCurrentLocationView = (EditText) findViewById(R.id.input_current_location);
        mCaseQtyView = (EditText) findViewById(R.id.input_case_qty);
        mLooseQtyView = (EditText) findViewById(R.id.input_loose_qty);
        mNewLocationView = (EditText) findViewById(R.id.input_new_location);
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
        Utilities.setEditMode(mButtonScanItem, mButtonScanCurrentLocation, mButtonScanNewLocation,
                mCurrentLocationView, mCaseQtyView, mLooseQtyView, mNewLocationView);
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
        setViewData();
    }

    /**
     * setViewMode
     * A method to set the layout on view mode
     */
    private void setViewMode() {
        Utilities.setViewMode(
                mButtonScanItem,
                mButtonScanCurrentLocation,
                mButtonScanNewLocation,
                mCurrentLocationView,
                mCaseQtyView,
                mLooseQtyView,
                mNewLocationView);
        setViewData();
    }
    /**
     * setViewData
     * A method to set the views with transaction date
     */
    private void setViewData() {
        RealmResults<Transaction> transactions = DataUtilities.getTransaction(getApplicationContext(), mTransactionId);
        if (transactions.size() > 0) {
            Transaction transaction = transactions.get(0);
            setNewLocationView(transaction.getLocationEnd());
            setCurrentLocationView(transaction.getLocationStart());
            setQtyViews(transaction.getQtyCasesString(), transaction.getQtyLooseString());
            setItemViews(
                    transaction.getSkuString(),
                    transaction.getItemDescription(),
                    transaction.getPackSize(),
                    transaction.getReceivedDate()
            );
        }
    }
    /**
     * scan
     * A method to initiate the barcode scanning
     */
    private void scan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    /**
     * save
     * A method to save the transaction record
     *
     * @return 1 for succes, 0 for error
     */
    private int save() {
        TextView inputSku = (TextView) findViewById(R.id.input_item_sku);
        TextView inputItemDescription = (TextView) findViewById(R.id.input_item_description);
        TextView inputLocationCurrent = (TextView) findViewById(R.id.input_current_location);
        TextView inputPackSize = (TextView) findViewById(R.id.input_pack_size);
        TextView inputReceivedDate = (TextView) findViewById(R.id.input_received_date);
        EditText inputNewLocation = (EditText) findViewById(R.id.input_new_location);
        EditText inputCaseQty = (EditText) findViewById(R.id.input_case_qty);
        EditText inputLooseQty = (EditText) findViewById(R.id.input_loose_qty);
        // Get input
        String skuString = inputSku.getText().toString();
        String itemDescription = inputItemDescription.getText().toString();
        String packSize = inputPackSize.getText().toString();
        String receivedDate = inputReceivedDate.getText().toString();
        String caseQtyString = inputCaseQty.getText().toString();
        String looseQtyString = inputLooseQty.getText().toString();
        String currentLocation = inputLocationCurrent.getText().toString();
        String newLocation = inputNewLocation.getText().toString();
        APIResponse apiResponse = DataUtilities.saveTransaction(
                getApplicationContext(),
                "Moving",
                "save",
                mTransactionId,
                mItemId,
                skuString,
                itemDescription,
                packSize,
                receivedDate,
                caseQtyString,
                looseQtyString,
                currentLocation,
                newLocation);
        Utilities.showToast(getApplicationContext(), apiResponse.getResponseText(), Toast.LENGTH_SHORT);
        if (apiResponse.getResponseCode() == 200) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * deleteTransaction
     * A caller method to delete a transaction
     *
     * @param transactionId the transaction id
     */
    private void deleteTransaction(String transactionId) {
        APIResponse apiResponse = DataUtilities.deleteTransaction(getApplicationContext(), transactionId);
        Utilities.showToast(getApplicationContext(), apiResponse.getResponseText(), Toast.LENGTH_SHORT);
        if (apiResponse.getResponseCode() == 200) {
            finish();
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
    private void setItemViews(String sku, String description, String packSize, String receivedDate) {
        TextView inputItemSku = (TextView) findViewById(R.id.input_item_sku);
        TextView inputItemDescription = (TextView) findViewById(R.id.input_item_description);
        TextView inputPackSize = (TextView) findViewById(R.id.input_pack_size);
        TextView inputReceivedDate = (TextView) findViewById(R.id.input_received_date);
        Utilities.setItemViews(
                inputItemSku,
                inputItemDescription,
                inputPackSize,
                inputReceivedDate,
                sku,
                description,
                packSize,
                receivedDate);
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
        TextView inputLooseQty = (TextView) findViewById(R.id.input_loose_qty);
        Utilities.setQtys(
                inputCaseQty,
                inputLooseQty,
                caseQty,
                looseQty);
    }

    /**
     * setCurrentLocationView
     *
     * @param location
     */
    private void setCurrentLocationView(String location) {
        TextView inputCurrentLocation = (TextView) findViewById(R.id.input_current_location);
        Utilities.setCurrentLocationView(
                inputCurrentLocation,
                location);
    }

    /**
     * setNewLocationView
     *
     * @param location
     */
    private void setNewLocationView(String location) {
        EditText inputNewLocation = (EditText) findViewById(R.id.input_new_location);
        Utilities.setCurrentLocationView(
                inputNewLocation,
                location);
    }

    /**
     * onActivityResult
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
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
        Context context = getApplicationContext();
        switch (mScanType) {
            case SCAN_ITEM:
                RealmResults<Item> items = DataUtilities.getItem(context, contents);
                if (items.size() > 0) {
                    Item record = items.get(0);
                    mItemId = record.getId();
                    String sku = Integer.toString(record.getSKU());
                    String description = record.getDescription();
                    String packSize = record.getPackSize();
                    String receivedDate = record.getReceivedDate();
                    setItemViews(sku, description, packSize, receivedDate);
                }else{
                    Utilities.showToast(context, "Item not found.", Toast.LENGTH_SHORT);
                }
                break;
            case SCAN_CURRENT_LOCATION:
                RealmResults<Location> currentLocations = DataUtilities.getLocation(context, contents);
                if (currentLocations.size() > 0) {
                    Location record = currentLocations.get(0);
                    String location = record.getLocation();
                    setCurrentLocationView(location);
                }else{
                    Utilities.showToast(context, "Location not found.", Toast.LENGTH_SHORT);
                }
                break;
            case SCAN_NEW_LOCATION:
                RealmResults<Location> newLocations = DataUtilities.getLocation(context, contents);
                if (newLocations.size() > 0) {
                    Location record = newLocations.get(0);
                    String location = record.getLocation();
                    setNewLocationView(location);
                }else{
                    Utilities.showToast(context, "Location not found.", Toast.LENGTH_SHORT);
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
                    setViewMode();
                    mMode = MODE_VIEW;
                    invalidateOptionsMenu();
                }
                return super.onOptionsItemSelected(item);
            case R.id.scan_edit:
                setEditMode();
                mMode = MODE_EDIT;
                invalidateOptionsMenu();
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(TRANSACTION_ID, mTransactionId);
        outState.putString(ITEM_ID, mItemId);
        outState.putString(MODE, mMode);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mTransactionId = savedInstanceState.getString(TRANSACTION_ID);
        mItemId = savedInstanceState.getString(ITEM_ID);
        mMode = savedInstanceState.getString(MODE);
        setViews();
        super.onRestoreInstanceState(savedInstanceState);
    }
}
