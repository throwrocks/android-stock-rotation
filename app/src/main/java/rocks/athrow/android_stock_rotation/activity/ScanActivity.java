package rocks.athrow.android_stock_rotation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.Item;
import rocks.athrow.android_stock_rotation.zxing.IntentIntegrator;
import rocks.athrow.android_stock_rotation.zxing.IntentResult;


/**
 * ScanActivity
 * Created by joselopez on 1/9/17.
 */

public class ScanActivity extends AppCompatActivity {
    private static final String SCAN_ITEM = "item";
    private static final String SCAN_LOCATION = "location";
    private String mRotationType;

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
        buttonScanItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan(SCAN_ITEM);
            }
        });

        /*if (action.equals(RotationActivity.ACTION_SCAN)) {
            scan(SCAN_ITEM);
        }*/

    }


    public void scan(String type) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setMessage(type);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult == null) {
            return;
        }
        String contents = scanResult.getContents();
        if (contents != null) {
            RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
            Realm.setDefaultConfiguration(realmConfig);
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmResults<Item> items = realm.where(Item.class).equalTo(Item.FIELD_TAG_NUMBER, contents).findAll();
            if ( items.size() > 0 ){
                Item item = items.get(0);
                String sku = Integer.toString(item.getSKU());
                String description = item.getDescription();
                String packSize = item.getPackSize();
                String receivedDate = item.getReceivedDate();
                String itemType = item.getItemType();
                setItemViews(sku, description, packSize, receivedDate, itemType);

            }
            realm.commitTransaction();
            realm.close();
        }
    }

    private void setItemViews(String sku, String description, String packSize, String receivedDate, String itemType){
        TextView inputItemSku = (TextView) findViewById(R.id.input_item_sku);
        TextView inputItemDescription = (TextView) findViewById(R.id.input_item_description);
        TextView inputPackSize = (TextView) findViewById(R.id.input_pack_size);
        TextView inputReceivedDate = (TextView) findViewById(R.id.input_received_date);
        inputItemSku.setText(sku);
        inputItemDescription.setText(description);
        inputPackSize.setText(packSize);
        inputReceivedDate.setText(receivedDate);
    }

}
