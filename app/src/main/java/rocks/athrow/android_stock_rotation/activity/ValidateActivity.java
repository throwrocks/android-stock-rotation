package rocks.athrow.android_stock_rotation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.adapter.ValidateAdapter;
import rocks.athrow.android_stock_rotation.api.APIRestFM;
import rocks.athrow.android_stock_rotation.api.APIResponse;
import rocks.athrow.android_stock_rotation.data.Comparison;
import rocks.athrow.android_stock_rotation.data.Item;
import rocks.athrow.android_stock_rotation.data.LocationItem;
import rocks.athrow.android_stock_rotation.data.ParseJSON;
import rocks.athrow.android_stock_rotation.data.RealmQueries;
import rocks.athrow.android_stock_rotation.util.Utilities;
import rocks.athrow.android_stock_rotation.zxing.IntentIntegrator;
import rocks.athrow.android_stock_rotation.zxing.IntentResult;

import static android.view.View.GONE;
import static rocks.athrow.android_stock_rotation.data.Constants.EMPTY;
import static rocks.athrow.android_stock_rotation.data.Constants.VALIDATE_BARCODE_CONTENTS;
import static rocks.athrow.android_stock_rotation.data.Constants.VALIDATE_INPUT_TEXT;
import static rocks.athrow.android_stock_rotation.data.Constants.VALIDATE_SCAN_TYPE;
import static rocks.athrow.android_stock_rotation.data.Constants.VALIDATE_TITLE;
import static rocks.athrow.android_stock_rotation.data.Constants.VALIDATE_TYPE_SKU;
import static rocks.athrow.android_stock_rotation.data.Constants.VALIDATE_TYPE_TAG;

/**
 * ValidateActivity
 * Created by joselopez on 1/27/17.
 */

public class ValidateActivity extends AppCompatActivity {
    private String mScanType;
    private String mBarcodeContents;
    private EditText mScanInput;
    private String mInputText;
    private String mSKU;
    private String mItemDescription;
    private TextView mSkuView;
    private TextView mItemDescriptionView;
    private RadioGroup mValidateRadioGroup;
    private LinearLayout mScanItem;
    private LinearLayout mResultHeaders;
    private RecyclerView mRecyclerView;
    private ArrayList<Comparison> mResults;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);
        mScanInput = (EditText) findViewById(R.id.validate_input);
        mValidateRadioGroup = (RadioGroup) findViewById(R.id.validate_type);
        RadioButton mValidateSkuType = (RadioButton) findViewById(R.id.validate_type_sku);
        RadioButton mValidateTagNumberType = (RadioButton) findViewById(R.id.validate_type_tag);
        mSkuView = (TextView) findViewById(R.id.validate_sku);
        mItemDescriptionView = (TextView) findViewById(R.id.validate_item_description);
        mResultHeaders = (LinearLayout) findViewById(R.id.validate_result_headers);
        mScanItem = (LinearLayout) findViewById(R.id.validate_scan_item);
        LinearLayout mScanButton = (LinearLayout) findViewById(R.id.validate_new_scan);
        mRecyclerView = (RecyclerView) findViewById(R.id.validate_results);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });
        mValidateSkuType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSku();
            }
        });
        mValidateTagNumberType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTagNumber();
            }
        });
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(VALIDATE_TITLE);
        }
        if ( savedInstanceState != null ){
            mInputText = savedInstanceState.getString(VALIDATE_INPUT_TEXT);
            mBarcodeContents = savedInstanceState.getString(VALIDATE_BARCODE_CONTENTS);
            mScanType = savedInstanceState.getString(VALIDATE_SCAN_TYPE);
            QueryAPI queryAPI = new QueryAPI();
            queryAPI.execute(mScanType);
        }else{
            hideRecyclerView();
        }
    }

    private void selectSku(){
        hideRecyclerView();
    }
    private void selectTagNumber(){
        hideRecyclerView();
    }

    /**
     * scan()
     */
    private void scan() {
        int typeId = mValidateRadioGroup.getCheckedRadioButtonId();
        if (typeId > 0) {
            RadioButton radioButton = (RadioButton) findViewById(typeId);
            mScanType = radioButton.getText().toString();
        }
        if (typeId > 0 && (mScanType.equals(VALIDATE_TYPE_SKU) || mScanType.equals(VALIDATE_TYPE_TAG))) {
            initiateScan();
        }
    }

    private void initiateScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
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
            Utilities.showToast(getApplicationContext(),
                    getResources().getString(R.string.item_not_found), Toast.LENGTH_SHORT);
            return;
        }
        mBarcodeContents = contents;
        QueryAPI queryAPI = new QueryAPI();
        queryAPI.execute(mScanType);
    }

    /**
     * UpdateCounts
     * AsyncTask to set the total counts, off the UI thread because of multiple database calls
     */
    private class QueryAPI extends AsyncTask<String, Void, ArrayList<Comparison>> {
        final Context context = getApplicationContext();
        private ArrayList<Comparison> getResults(String type, String searchCriteria) {
            ArrayList<Comparison> results = new ArrayList<>();
            ArrayList<LocationItem> fmResults;
            JSONArray edisonResults;
            switch (type) {
                case VALIDATE_TYPE_TAG:
                    mInputText = searchCriteria;
                    Comparison tagComparison = new Comparison();
                    APIResponse tagAPIResponse = APIRestFM.getItemByTag(searchCriteria);
                    String tagResponseText = tagAPIResponse.getResponseText();
                    edisonResults = ParseJSON.getJSONArray(tagResponseText);
                    if (edisonResults == null || edisonResults.length() == 0) {
                        return results;
                    }
                    JSONObject tagComparisonEdisonRecord = null;
                    try {
                        tagComparisonEdisonRecord = edisonResults.getJSONObject(0);
                        mItemDescription = tagComparisonEdisonRecord.getString(Item.FIELD_DESCRIPTION);
                        mSKU = tagComparisonEdisonRecord.getString(Item.FIELD_SKU);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (tagComparisonEdisonRecord == null) {
                        return results;
                    }
                    fmResults = RealmQueries.getLocationItems(context, Item.FIELD_TAG_NUMBER, searchCriteria);
                    if (fmResults != null && fmResults.size() > 0) {
                        tagComparison.setFmResults(fmResults);
                        tagComparison.setEdisonResult(tagComparisonEdisonRecord);
                        results.add(tagComparison);
                    }
                    break;
                case VALIDATE_TYPE_SKU:
                    mInputText = EMPTY;
                    int sku = RealmQueries.getSKUFromTag(context, searchCriteria);
                    if (sku == 0) {
                        return results;
                    }
                    APIResponse skuAPIResponse = APIRestFM.getItemBySKU(sku);
                    String skuResponseText = skuAPIResponse.getResponseText();
                    edisonResults = ParseJSON.getJSONArray(skuResponseText);
                    if (edisonResults == null || edisonResults.length() == 0) {
                        return results;
                    }
                    mSKU = String.valueOf(sku);
                    mInputText = mSKU;
                    int countEdisonResults = edisonResults.length();
                    for (int i = 0; i < countEdisonResults; i++) {
                        Comparison skuComparison = new Comparison();
                        try {
                            JSONObject edisonRecord = edisonResults.getJSONObject(i);
                            mItemDescription = edisonRecord.getString(Item.FIELD_DESCRIPTION);
                            fmResults = RealmQueries.getLocationItems(
                                    context, Item.FIELD_TAG_NUMBER,
                                    edisonRecord.getString(Item.FIELD_TAG_NUMBER)
                            );
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
            mScanInput.setText(mInputText);
            mResults = results;
            setupRecyclerView();
            super.onPostExecute(results);
        }
    }

    private void hideRecyclerView(){
        mScanInput.setText(EMPTY);
        mScanItem.setVisibility(GONE);
        mResultHeaders.setVisibility(GONE);
        mRecyclerView.setVisibility(GONE);
    }

    private void setupRecyclerView() {
        Context context = getApplicationContext();
        mScanItem.setVisibility(View.VISIBLE);
        mResultHeaders.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mSkuView.setText(mSKU);
        mItemDescriptionView.setText(mItemDescription);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        ValidateAdapter adapter = new ValidateAdapter(context, mResults);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(VALIDATE_INPUT_TEXT, mInputText);
        outState.putString(VALIDATE_BARCODE_CONTENTS, mBarcodeContents);
        outState.putString(VALIDATE_SCAN_TYPE, mScanType);
    }
}
