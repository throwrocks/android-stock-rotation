package rocks.athrow.android_stock_rotation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.api.API;
import rocks.athrow.android_stock_rotation.api.APIResponse;
import rocks.athrow.android_stock_rotation.data.LocationItem;
import rocks.athrow.android_stock_rotation.data.ParseJSON;
import rocks.athrow.android_stock_rotation.data.RealmQueries;
import rocks.athrow.android_stock_rotation.zxing.IntentIntegrator;
import rocks.athrow.android_stock_rotation.zxing.IntentResult;

import static rocks.athrow.android_stock_rotation.data.RealmQueries.getLocationItems;

/**
 * Created by joselopez on 1/27/17.
 */

public class ValidateActivity extends AppCompatActivity {
    private String mScanType;
    private String mBarcodeContents;
    private EditText mScanInput;
    private TextView mSkuView;
    private TextView mItemDescriptionView;
    private RadioGroup mValidateRadioGroup;
    private LinearLayout mScanButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);
        mScanInput = (EditText) findViewById(R.id.validate_input);
        mValidateRadioGroup = (RadioGroup) findViewById(R.id.validate_type);
        mSkuView = (TextView) findViewById(R.id.validate_sku);
        mItemDescriptionView = (TextView) findViewById(R.id.validate_item_description);
        mScanButton = (LinearLayout) findViewById(R.id.validate_new_scan);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });
    }

    private void scan() {
        int typeId = mValidateRadioGroup.getCheckedRadioButtonId();
        if (typeId > 0 ) {
            RadioButton radioButton = (RadioButton) findViewById(typeId);
            mScanType = radioButton.getText().toString();
        }
        Log.d("type id ", "" + typeId);
        Log.d("type selection ", "" + mScanType);
        if ( typeId > 0 && ( mScanType.equals("SKU") || mScanType.equals("Tag #"))){
            initiateScan();
        }
    }

    public void initiateScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

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
        mScanInput.setText(mBarcodeContents);
        QueryAPI queryAPI = new QueryAPI();
        queryAPI.execute(mScanType);
    }

    /**
     * UpdateCounts
     * AsyncTask to set the total counts, off the UI thread because of multiple database calls
     */
    private class QueryAPI extends AsyncTask<String, Void, ArrayList<LocationItem>> {
        Context context = getApplicationContext();

        @Override
        protected ArrayList<LocationItem> doInBackground(String... params) {
            APIResponse apiResponse = API.getItemByTag(mBarcodeContents);
            String responseText = apiResponse.getResponseText();
            JSONArray results = ParseJSON.getJSONArray(responseText);
            ArrayList<LocationItem> locationItems = null;
            if ( results != null && results.length() > 0 ) {
                locationItems = RealmQueries.getLocationItems(context, "tagNumber", mBarcodeContents);
            }
            return locationItems;
        }

        @Override
        protected void onPostExecute(ArrayList<LocationItem> locationItems) {
            if ( locationItems == null ){
                return;
            }
            int size = locationItems.size();
            if ( size == 0 ){
                return;
            }
            LocationItem item = locationItems.get(0);
            mSkuView.setText(item.getSKU());
            mItemDescriptionView.setText(item.getDescription());
            super.onPostExecute(locationItems);

        }
    }
}
