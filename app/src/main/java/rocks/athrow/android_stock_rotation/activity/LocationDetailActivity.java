package rocks.athrow.android_stock_rotation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.adapter.LocationDetailsAdapter;
import rocks.athrow.android_stock_rotation.data.LocationItem;
import rocks.athrow.android_stock_rotation.data.RealmQueries;

/**
 * LocationDetailActivity
 * Created by jose on 1/15/17.
 */

public class LocationDetailActivity extends AppCompatActivity {
    private String mLocation;
    private LocationDetailsAdapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);
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
        ArrayList<LocationItem> items = RealmQueries.getLocationItems(getApplicationContext(), "location", mLocation);
        if ( items == null ){
            return;
        }
        mAdapter = new LocationDetailsAdapter(items);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.location_details_container);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);
    }
}
