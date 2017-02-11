package rocks.athrow.android_stock_rotation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.adapter.ValidateAdapter;
import rocks.athrow.android_stock_rotation.api.API;
import rocks.athrow.android_stock_rotation.api.APIResponse;
import rocks.athrow.android_stock_rotation.data.Comparison;
import rocks.athrow.android_stock_rotation.data.Item;
import rocks.athrow.android_stock_rotation.data.LocationItem;
import rocks.athrow.android_stock_rotation.data.ParseJSON;
import rocks.athrow.android_stock_rotation.data.RealmQueries;
import rocks.athrow.android_stock_rotation.zxing.IntentIntegrator;
import rocks.athrow.android_stock_rotation.zxing.IntentResult;

import static rocks.athrow.android_stock_rotation.data.RealmQueries.getLocationItems;

/**
 * ValidateActivity
 * Created by joselopez on 1/27/17.
 */

public class ValidateActivity extends AppCompatActivity {
    private String mScanType;
    private String mBarcodeContents;
    private EditText mScanInput;
    private String mSKU;
    private String mItemDescription;
    private TextView mSkuView;
    private TextView mItemDescriptionView;
    private RadioGroup mValidateRadioGroup;
    private LinearLayout mHeaders;
    private LinearLayout mScanButton;
    private RecyclerView mRecyclerView;
    private ArrayList<Comparison> mResults;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);
        mScanInput = (EditText) findViewById(R.id.validate_input);
        mValidateRadioGroup = (RadioGroup) findViewById(R.id.validate_type);
        mSkuView = (TextView) findViewById(R.id.validate_sku);
        mItemDescriptionView = (TextView) findViewById(R.id.validate_item_description);
        mHeaders = (LinearLayout) findViewById(R.id.validate_result_headers);
        mScanButton = (LinearLayout) findViewById(R.id.validate_new_scan);
        mRecyclerView = (RecyclerView) findViewById(R.id.validate_results);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });
        hideRecyclerView();
    }

    private void scan() {
        int typeId = mValidateRadioGroup.getCheckedRadioButtonId();
        if (typeId > 0) {
            RadioButton radioButton = (RadioButton) findViewById(typeId);
            mScanType = radioButton.getText().toString();
        }
        Log.d("type id ", "" + typeId);
        Log.d("type selection ", "" + mScanType);
        if (typeId > 0 && (mScanType.equals("SKU") || mScanType.equals("Tag #"))) {
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
    private class QueryAPI extends AsyncTask<String, Void, ArrayList<Comparison>> {
        Context context = getApplicationContext();

        private ArrayList<Comparison> getResults(String type, String searchCriteria) {
            ArrayList<Comparison> results = new ArrayList<>();
            ArrayList<LocationItem> fmResults;
            JSONArray edisonResults;
            switch (type) {
                case "Tag #":
                    Comparison tagComparison = new Comparison();
                    APIResponse tagAPIResponse = API.getItemByTag(searchCriteria);
                    String tagResponseText = tagAPIResponse.getResponseText();
                    edisonResults = ParseJSON.getJSONArray(tagResponseText);
                    if (edisonResults == null || edisonResults.length() > 0) {
                        return results;
                    }
                    JSONObject tagComparisonEdisonRecord = null;
                    try {
                        tagComparisonEdisonRecord = edisonResults.getJSONObject(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (tagComparisonEdisonRecord == null) {
                        return results;
                    }
                    fmResults = RealmQueries.getLocationItems(context, "tagNumber", searchCriteria);
                    if (fmResults != null && fmResults.size() > 0) {
                        tagComparison.setFmResults(fmResults);
                        tagComparison.setEdisonResult(tagComparisonEdisonRecord);
                        results.add(tagComparison);
                    }
                    break;
                case "SKU":
                    int sku = RealmQueries.getSKUFromTag(context, searchCriteria);
                    if (sku == 0) {
                        return results;
                    }
                    APIResponse skuAPIResponse = API.getItemBySKU(sku);
                    String skuResponseText = skuAPIResponse.getResponseText();
                    edisonResults = ParseJSON.getJSONArray(skuResponseText);
                    if (edisonResults == null || edisonResults.length() == 0) {
                        return results;
                    }
                    mSKU = String.valueOf(sku);
                    int countEdisonResults = edisonResults.length();
                    for (int i = 0; i < countEdisonResults; i++) {
                        Comparison skuComparison = new Comparison();
                        try {
                            JSONObject edisonRecord = edisonResults.getJSONObject(i);
                            mItemDescription = edisonRecord.getString(Item.FIELD_DESCRIPTION);
                            fmResults = RealmQueries.getLocationItems(context, "tagNumber", edisonRecord.getString(Item.FIELD_TAG_NUMBER));
                            if ((fmResults != null && fmResults.size() > 0) || edisonRecord.getInt(Item.FIELD_EDISON_QTY) > 0) {
                                skuComparison.setEdisonResult(edisonRecord);
                                skuComparison.setFmResults(fmResults);
                                results.add(skuComparison);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
            return results;
        }

        @Override
        protected ArrayList<Comparison> doInBackground(String... params) {
            return getResults(mScanType, mBarcodeContents);
        }

        @Override
        protected void onPostExecute(ArrayList<Comparison> results) {
            if (results == null) {
                hideRecyclerView();
                return;
            }
            int size = results.size();
            if (size == 0) {
                hideRecyclerView();
                return;
            }
            mResults = results;
            mSkuView.setText(mSKU);
            mItemDescriptionView.setText(mItemDescription);
            setupRecyclerView();
            super.onPostExecute(results);
        }
    }

    private void hideRecyclerView(){
        mHeaders.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        Context context = getApplicationContext();
        mHeaders.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        ValidateAdapter adapter = new ValidateAdapter(context, mResults);
        mRecyclerView.setAdapter(adapter);
    }
}
