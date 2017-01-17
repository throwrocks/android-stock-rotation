package rocks.athrow.android_stock_rotation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.LocationItem;
import rocks.athrow.android_stock_rotation.data.RealmQueries;

/**
 * LocationDetailActivity
 * Created by jose on 1/15/17.
 */

public class LocationDetailActivity extends AppCompatActivity {
    private String mLocation;
    private LinearLayout mContainer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);
        mContainer = (LinearLayout) findViewById(R.id.location_details_container);
        Intent intent = getIntent();
        if (intent != null) {
            mLocation = intent.getStringExtra("location");
        }
        TextView locationView = (TextView) findViewById(R.id.location_details_location);
        locationView.setText(mLocation);
        setupItems();
    }

    /**
     * setUpItems
     */
    private void setupItems(){
        ArrayList<LocationItem> items = RealmQueries.findItemsWithLocations(getApplicationContext(), mLocation, null);
        if ( items == null ){
            return;
        }
        int size = items.size();
        if ( size > 0){
            for (int i = 0; i < size; i++) {
                LocationItem item = items.get(i);
                String itemSku = String.valueOf(item.getSKU());
                String itemDescription = item.getDescription();
                String packSize = item.getPackSize();
                String receivedDate = item.getReceivedDate();
                String countCases = item.getCaseQty();
                View view = getLayoutInflater().inflate(R.layout.content_location_details_item, mContainer, false);
                TextView skuView = (TextView) view.findViewById(R.id.location_details_item_sku);
                TextView itemDescriptionView = (TextView) view.findViewById(R.id.location_details_item_description);
                TextView packSizeView = (TextView) view.findViewById(R.id.location_details_pack_size);
                TextView receivedDateView = (TextView) view.findViewById(R.id.location_details_received_date);
                TextView casesView = (TextView) view.findViewById(R.id.location_details_cases_qty);
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
