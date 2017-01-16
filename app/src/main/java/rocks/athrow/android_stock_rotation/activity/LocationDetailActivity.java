package rocks.athrow.android_stock_rotation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.DataUtilities;
import rocks.athrow.android_stock_rotation.data.Item;

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

        ArrayList<Item> items = DataUtilities.getLocationItems(getApplicationContext(), mLocation);
        int size = items.size();
        if ( size > 0){
            for (int i = 0; i < size; i++) {
                View view = getLayoutInflater().inflate(R.layout.content_location_details_item, mContainer, false);
                mContainer.addView(view);
            }
        }

    }
}
