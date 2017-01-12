package rocks.athrow.android_stock_rotation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.Item;
import rocks.athrow.android_stock_rotation.data.Location;
import rocks.athrow.android_stock_rotation.data.Transaction;
import rocks.athrow.android_stock_rotation.zxing.IntentIntegrator;
import rocks.athrow.android_stock_rotation.zxing.IntentResult;


/**
 * ScanActivity
 * Created by joselopez on 1/9/17.
 */

public class ScanActivity extends AppCompatActivity {
    private static final String SCAN_ITEM = "item";
    private static final String SCAN_CURRENT_LOCATION = "currentLocation";
    private static final String SCAN_NEW_LOCATION = "newLocation";
    private String mTransactionId;
    private String mRotationType;
    private String mScanType;
    private String mItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Intent intent = getIntent();
        mRotationType = intent.getStringExtra(MainActivity.MODULE_TYPE);
        String action = intent.getStringExtra(RotationActivity.ADD_ITEM_ACTION);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        LinearLayout buttonScanItem = (LinearLayout) findViewById(R.id.scan_item);
        LinearLayout buttonScanCurrentLocation = (LinearLayout) findViewById(R.id.scan_current_location);
        LinearLayout buttonScanNewLocation = (LinearLayout) findViewById(R.id.scan_new_location);
        LinearLayout buttonQueue = (LinearLayout) findViewById(R.id.button_queue);
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
        buttonQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queue();
            }
        });
        // Create a transaction record
        mTransactionId = UUID.randomUUID().toString();
        Date today = new Date();
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Transaction transaction = new Transaction();
        transaction.setId(mTransactionId);
        transaction.setType1(mRotationType);
        transaction.setIsCompleted(false);
        transaction.setDate(today);
        realm.copyToRealmOrUpdate(transaction);
        realm.commitTransaction();
        realm.close();
        /*if (action.equals(RotationActivity.ACTION_SCAN)) {
            scan(SCAN_ITEM);
        }*/
    }


    private void scan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    private void queue(){
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
        if ( skuString.isEmpty()){
            return;
        }
        // TODO: Validate according to action (queue vs move)
        if ( caseQtyString.isEmpty() && looseQtyString.isEmpty() ){
            return;
        }
        if ( currentLocation.isEmpty()){
            return;
        }
        // Set data variables
        int Sku = Integer.parseInt(skuString);
        int caseQty = 0;
        if ( !caseQtyString.isEmpty()){
            caseQty = Integer.parseInt(caseQtyString);
        }
        int looseQty = 0;
        if ( !looseQtyString.isEmpty()){
            looseQty = Integer.parseInt(looseQtyString);
        }
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Transaction> transactions = realm.where(Transaction.class).equalTo(Transaction.ID, mTransactionId).findAll();
        if ( transactions.size() > 0 ){
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
            finish();
        }else{
            realm.commitTransaction();
            realm.close();
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
                    String itemType = record.getItemType();
                    setItemViews(sku, description, packSize, receivedDate, itemType);
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

    private void setItemViews(String sku, String description, String packSize, String receivedDate, String itemType) {
        TextView inputItemSku = (TextView) findViewById(R.id.input_item_sku);
        TextView inputItemDescription = (TextView) findViewById(R.id.input_item_description);
        TextView inputPackSize = (TextView) findViewById(R.id.input_pack_size);
        TextView inputReceivedDate = (TextView) findViewById(R.id.input_received_date);
        inputItemSku.setText(sku);
        inputItemDescription.setText(description);
        inputPackSize.setText(packSize);
        inputReceivedDate.setText(receivedDate);
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
