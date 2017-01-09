package rocks.athrow.android_stock_rotation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.zxing.IntentIntegrator;
import rocks.athrow.android_stock_rotation.zxing.IntentResult;

/**
 * ScanActivity
 * Created by joselopez on 1/9/17.
 */

public class ScanActivity extends AppCompatActivity {

    private static final String SCAN_ITEM = "item";
    private static final String SCAN_LOCATION = "location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotation);
        Intent intent = getIntent();
        String rotationType = intent.getStringExtra(MainActivity.MODULE_TYPE);
        scan(SCAN_ITEM);
    }

    public void scan(String type){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String contents = scanResult.getContents();
            // TODO: Search in database and populate the views
        }

    }

}
