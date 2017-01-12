package rocks.athrow.android_stock_rotation.activity;

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

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.Item;
import rocks.athrow.android_stock_rotation.data.Location;
import rocks.athrow.android_stock_rotation.data.Transaction;
import rocks.athrow.android_stock_rotation.zxing.IntentIntegrator;
import rocks.athrow.android_stock_rotation.zxing.IntentResult;

import static android.R.attr.mode;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static rocks.athrow.android_stock_rotation.activity.RotationActivity.ADD_ITEM_ACTION;


/**
 * ScanActivity
 * Created by joselopez on 1/9/17.
 */

public class ScanActivity extends AppCompatActivity {
    public static final String ITEM_ID = "item_id";
    public static final String TRANSACTION_ID = "transaction_id";
    public static final String MODE = "mode";
    public static final String MODE_EDIT = "edit";
    public static final String MODE_VIEW = "view";
    private static final String SCAN_TYPE = "scan_type";
    private static final String SCAN_ITEM = "item";
    private static final String SCAN_CURRENT_LOCATION = "currentLocation";
    private static final String SCAN_NEW_LOCATION = "newLocation";
    private String mTransactionId;
    private String mRotationType;
    private String mScanType;
    private String mItemId;
    private String mMode;
    LinearLayout buttonScanItem;
    LinearLayout buttonScanCurrentLocation;
    LinearLayout buttonScanNewLocation;
    LinearLayout buttonQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Intent intent = getIntent();
        if (intent != null) {
            mTransactionId = intent.getStringExtra(TRANSACTION_ID);
            mItemId = intent.getStringExtra(ITEM_ID);
            mRotationType = intent.getStringExtra(MainActivity.MODULE_TYPE);
            mMode = intent.getStringExtra(MODE);
            String action = intent.getStringExtra(ADD_ITEM_ACTION);
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        buttonScanItem = (LinearLayout) findViewById(R.id.scan_item);
        buttonScanCurrentLocation = (LinearLayout) findViewById(R.id.scan_current_location);
        buttonScanNewLocation = (LinearLayout) findViewById(R.id.scan_new_location);
        buttonQueue = (LinearLayout) findViewById(R.id.button_queue);
        if (mMode.equals(MODE_EDIT)) {
            setEditMode();
        } else {
            setViewMode();
        }
        buttonQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queue();
            }
        });

        /*if (action.equals(RotationActivity.ACTION_SCAN)) {
            scan(SCAN_ITEM);
        }*/
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
            case R.id.scan_save:
                int save = save();
                if ( save == 1 ){
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
        outState.putString(MainActivity.MODULE_TYPE, mRotationType);
        outState.putString(SCAN_TYPE, mScanType);
        outState.putString(TRANSACTION_ID, mTransactionId);
        outState.putString(ITEM_ID, mItemId);
        outState.putString(MODE, mMode);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mRotationType = savedInstanceState.getString(MainActivity.MODULE_TYPE);
        mScanType = savedInstanceState.getString(SCAN_TYPE);
        mTransactionId = savedInstanceState.getString(TRANSACTION_ID);
        mItemId = savedInstanceState.getString(ITEM_ID);
        mMode = savedInstanceState.getString(MODE);
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setEditMode() {
        RealmResults<Transaction> transactions = getTransaction(mTransactionId);
        if (transactions.size() > 0) {
            Transaction transaction = transactions.get(0);
            setNewLocationView(transaction.getLocationEnd());
            setCurrentLocationView(transaction.getLocationStart());
            setQtys(transaction.getQtyCasesString(), transaction.getQtyLooseString());
        }
        RealmResults<Item> items = getItem(mItemId);
        if (items.size() > 0) {
            Item item = items.get(0);
            buttonScanItem.setVisibility(GONE);
            buttonScanCurrentLocation.setVisibility(GONE);
            buttonScanNewLocation.setVisibility(GONE);
            setItemViews(
                    String.valueOf(item.getSKU()),
                    item.getDescription(),
                    item.getPackSize(),
                    item.getReceivedDate()
            );
        }
        buttonScanItem.setVisibility(VISIBLE);
        buttonScanCurrentLocation.setVisibility(VISIBLE);
        buttonScanNewLocation.setVisibility(VISIBLE);
        buttonScanItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScanType = SCAN_ITEM;
                scan();
            }
        });
        buttonScanCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScanType = SCAN_CURRENT_LOCATION;
                scan();
            }
        });
        buttonScanNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScanType = SCAN_NEW_LOCATION;
                scan();
            }
        });
    }

    private void setViewMode() {
        buttonScanItem.setVisibility(GONE);
        buttonScanCurrentLocation.setVisibility(GONE);
        buttonScanNewLocation.setVisibility(GONE);

        RealmResults<Transaction> transactions = getTransaction(mTransactionId);
        if (transactions.size() > 0) {
            Transaction transaction = transactions.get(0);
            setNewLocationView(transaction.getLocationEnd());
            setCurrentLocationView(transaction.getLocationStart());
            setQtys(transaction.getQtyCasesString(), transaction.getQtyLooseString());
        }

        RealmResults<Item> items = getItem(mItemId);
        if (items.size() > 0) {
            Item item = items.get(0);
            buttonScanItem.setVisibility(GONE);
            buttonScanCurrentLocation.setVisibility(GONE);
            buttonScanNewLocation.setVisibility(GONE);
            setItemViews(
                    String.valueOf(item.getSKU()),
                    item.getDescription(),
                    item.getPackSize(),
                    item.getReceivedDate()
            );
        }
    }

    private RealmResults<Transaction> getTransaction(String transactionId) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Transaction> realmResults = realm.where(Transaction.class).equalTo(Transaction.ID, transactionId).findAll();
        realm.beginTransaction();
        realm.commitTransaction();
        return realmResults;
    }

    private RealmResults<Item> getItem(String itemId) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Item> realmResults = realm.where(Item.class).equalTo(Item.FIELD_ID, itemId).findAll();
        realm.beginTransaction();
        realm.commitTransaction();
        return realmResults;
    }

    private void scan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    private int save() {
        TextView inputSku = (TextView) findViewById(R.id.input_item_sku);
        TextView inputItemDescription = (TextView) findViewById(R.id.input_item_description);
        TextView inputLocationCurrent = (TextView) findViewById(R.id.input_current_location);
        EditText inputNewLocation = (EditText) findViewById(R.id.input_new_location);
        EditText inputCaseQty = (EditText) findViewById(R.id.input_case_qty);
        EditText inputLooseQty = (EditText) findViewById(R.id.input_loose_qty);
        // Get input
        String skuString = inputSku.getText().toString();
        String itemDescription = inputItemDescription.getText().toString();
        String caseQtyString = inputCaseQty.getText().toString();
        String looseQtyString = inputLooseQty.getText().toString();
        String currentLocation = inputLocationCurrent.getText().toString();
        String newLocation = inputNewLocation.getText().toString();
        // Validate
        if (skuString.isEmpty() || skuString.equals("No item selected")) {
            return 0;
        }
        // TODO: Validate according to action (queue vs move)
        if (caseQtyString.isEmpty() && looseQtyString.isEmpty()) {
            return 0;
        }
        if (currentLocation.isEmpty() || currentLocation.equals("N/A")) {
            return 0;
        }
        if (newLocation.isEmpty() || currentLocation.equals("N/A")) {
            newLocation = "";
        }
        // Set data variables
        int Sku = Integer.parseInt(skuString);
        int caseQty = 0;
        if (!caseQtyString.isEmpty()) {
            caseQty = Integer.parseInt(caseQtyString);
        }
        int looseQty = 0;
        if (!looseQtyString.isEmpty()) {
            looseQty = Integer.parseInt(looseQtyString);
        }
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Transaction> transactions = realm.where(Transaction.class).equalTo(Transaction.ID, mTransactionId).findAll();
        if (transactions.size() > 0) {
            Transaction transaction = transactions.get(0);
            transaction.setItemId(mItemId);
            transaction.setSku(Sku);
            transaction.setItemDescription(itemDescription);
            transaction.setType2("");
            transaction.setLocationStart(currentLocation);
            transaction.setLocationEnd(newLocation);
            transaction.setQtyCases(caseQty);
            transaction.setQtyLoose(looseQty);
            realm.copyToRealmOrUpdate(transaction);
            realm.commitTransaction();
            realm.close();
            return 1;
        } else {
            realm.commitTransaction();
            realm.close();
            return 0;
        }
    }

    private void queue() {
        int save = save();
        if ( save == 1 ){
            finish();
        }

    }

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
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        switch (mScanType) {
            case SCAN_ITEM:
                RealmResults<Item> items = realm.where(Item.class).equalTo(Item.FIELD_TAG_NUMBER, contents).findAll();
                if (items.size() > 0) {
                    Item record = items.get(0);
                    mItemId = record.getId();
                    String sku = Integer.toString(record.getSKU());
                    String description = record.getDescription();
                    String packSize = record.getPackSize();
                    String receivedDate = record.getReceivedDate();
                    setItemViews(sku, description, packSize, receivedDate);
                }
                break;
            case SCAN_CURRENT_LOCATION:
                RealmResults<Location> currentLocations = realm.where(Location.class).equalTo(Location.FIELD_BARCODE, contents).findAll();
                if (currentLocations.size() > 0) {
                    Location record = currentLocations.get(0);
                    String location = record.getLocation();
                    setCurrentLocationView(location);
                }
                break;
            case SCAN_NEW_LOCATION:
                RealmResults<Location> newLocations = realm.where(Location.class).equalTo(Location.FIELD_BARCODE, contents).findAll();
                if (newLocations.size() > 0) {
                    Location record = newLocations.get(0);
                    String location = record.getLocation();
                    setNewLocationView(location);
                }
                break;
        }
        realm.commitTransaction();
        realm.close();
    }

    private void setItemViews(String sku, String description, String packSize, String receivedDate) {
        TextView inputItemSku = (TextView) findViewById(R.id.input_item_sku);
        TextView inputItemDescription = (TextView) findViewById(R.id.input_item_description);
        TextView inputPackSize = (TextView) findViewById(R.id.input_pack_size);
        TextView inputReceivedDate = (TextView) findViewById(R.id.input_received_date);
        inputItemSku.setText(sku);
        inputItemDescription.setText(description);
        inputPackSize.setText(packSize);
        inputReceivedDate.setText(receivedDate);
    }

    private void setQtys(String caseQty, String looseQty){
        TextView inputCaseQty = (TextView) findViewById(R.id.input_case_qty);
        TextView inputLooseQty = (TextView) findViewById(R.id.input_loose_qty);
        inputCaseQty.setText(caseQty);
        inputLooseQty.setText(looseQty);
    }

    private void setCurrentLocationView(String location) {
        TextView inputCurrentLocation = (TextView) findViewById(R.id.input_current_location);
        inputCurrentLocation.setText(location);
    }

    private void setNewLocationView(String location) {
        EditText inputNewLocation = (EditText) findViewById(R.id.input_new_location);
        inputNewLocation.setText(location);
    }

}
