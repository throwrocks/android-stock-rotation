package rocks.athrow.android_stock_rotation.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.Item;
import rocks.athrow.android_stock_rotation.data.LocationItem;
import rocks.athrow.android_stock_rotation.data.RealmQueries;
import rocks.athrow.android_stock_rotation.util.PreferencesHelper;

/**
 * Created by jose on 1/15/17.
 */

public class SearchActivity extends AppCompatActivity {
    private ArrayList<LocationItem> mItems;
    private String mSearchCriteria;
    private EditText mSearchField;
    private LinearLayout mContainer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getApplicationContext();
        setContentView(R.layout.activity_search);
        mContainer = (LinearLayout) findViewById(R.id.search_items_container);
        mSearchField = (EditText) findViewById(R.id.search_items);
        mSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mSearchCriteria = mSearchField.getText().toString();
                    PreferencesHelper preferencesHelper = new PreferencesHelper(context);
                    preferencesHelper.save("items_search_criteria", mSearchCriteria);
                    mItems= RealmQueries.findItemsWithLocations(getApplicationContext(), null, mSearchCriteria);
                    setupItems(mItems);
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    handled = true;
                }
                return handled;
            }
        });

    }

    /**
     * setUpItems
     */
    private void setupItems(ArrayList<LocationItem> items){
        if ( items == null ){
            return;
        }
        int size = items.size();
        if ( size > 0){
            mContainer.removeAllViews();
            for (int i = 0; i < size; i++) {
                LocationItem item = items.get(i);
                String location = item.getLocation();
                String itemSku = String.valueOf(item.getSKU());
                String itemDescription = item.getDescription();
                String packSize = item.getPackSize();
                String receivedDate = item.getReceivedDate();
                String countCases = item.getCaseQty();
                View view = getLayoutInflater().inflate(R.layout.content_search_items_item, mContainer, false);
                TextView locationView = (TextView) view.findViewById(R.id.search_items_location);
                TextView skuView = (TextView) view.findViewById(R.id.search_items_item_sku);
                TextView itemDescriptionView = (TextView) view.findViewById(R.id.search_items_item_description);
                TextView packSizeView = (TextView) view.findViewById(R.id.search_items_pack_size);
                TextView receivedDateView = (TextView) view.findViewById(R.id.search_items_received_date);
                TextView casesView = (TextView) view.findViewById(R.id.search_items_cases_qty);
                locationView.setText(location);
                skuView.setText(itemSku);
                itemDescriptionView.setText(itemDescription);
                packSizeView.setText(packSize);
                receivedDateView.setText(receivedDate);
                casesView.setText(countCases);
                mContainer.addView(view);
            }
        }
    }

}
