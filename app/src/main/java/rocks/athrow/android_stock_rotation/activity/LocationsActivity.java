package rocks.athrow.android_stock_rotation.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.ArrayList;

import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.adapter.LocationRowsAdapter;
import rocks.athrow.android_stock_rotation.adapter.LocationsAdapter;
import rocks.athrow.android_stock_rotation.data.Location;
import rocks.athrow.android_stock_rotation.data.LocationRows;
import rocks.athrow.android_stock_rotation.data.RealmQueries;
import rocks.athrow.android_stock_rotation.realmadapter.RealmLocationsListAdapter;
import rocks.athrow.android_stock_rotation.util.PreferencesHelper;


/**
 * LocationsActivity
 * Created by jose on 1/15/17.
 */

public class LocationsActivity extends AppCompatActivity {
    private final static String LOCATIONS_FILTER = "locations_filter";
    private final static String LOCATIONS_FILTER_ROW = "locations_search_criteria";
    private final static String LOCATIONS_FILTER_PRIMARY_ONLY = "locations_filter_primary_only";
    private final static String EMPTY = "";
    private final static String ALL = "All";
    private final static String FREEZER = "Freezer";
    private final static String COOLER = "Cooler";
    private final static String PAPER = "Paper";
    private final static String DRY = "Dry";
    private final static CharSequence[] SEARCH_FILTERS = {FREEZER, COOLER, DRY, PAPER, ALL};
    private String mLocationsFilter;
    private CheckBox mPrimaryCheckBox;
    private boolean mPrimaryOnly;
    private RealmResults<Location> mRealmResults;
    private ArrayList<LocationRows> mRows;
    private String mSelectedRow;
    private Spinner mSpinner;
    private LocationRowsAdapter mRowAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mSpinner = (Spinner) findViewById(R.id.locations_spinner);
        mPrimaryCheckBox = (CheckBox) findViewById(R.id.locations_primary);
        ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SEARCH_FILTERS);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(spinnerAdapter);
        /** Spinner Click Listener **/
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                filterLocations(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /** Primary Only Click Listener **/
        mPrimaryCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPrimaryOnly(mPrimaryCheckBox.isChecked());
            }
        });
    }

    private void updateRealmResults() {
        Context context = getApplicationContext();
        mRealmResults = RealmQueries.getLocations(context, mLocationsFilter, mSelectedRow, mPrimaryOnly);

    }

    private void filterLocations(String type) {
        Context context = getApplicationContext();
        mLocationsFilter = type;
        mRows = RealmQueries.getRows(context, mLocationsFilter);
        PreferencesHelper preferencesHelper = new PreferencesHelper(context);
        preferencesHelper.save(LOCATIONS_FILTER, type);
        preferencesHelper.save(LOCATIONS_FILTER_ROW, mSelectedRow);
        updateRealmResults();
        setupRowResults();
        setupLocationResults();
    }

    public void setRow(String row){
        mSelectedRow = row;
        updateRealmResults();
        setupLocationResults();

    }

    private void setupRowResults() {
        mRowAdapter = new LocationRowsAdapter(mRows, LocationsActivity.this, mSelectedRow);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.location_rows);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mRowAdapter);
    }

    private void setPrimaryOnly(boolean primaryOnly) {
        Context context = getApplicationContext();
        PreferencesHelper preferencesHelper = new PreferencesHelper(context);
        preferencesHelper.save(LOCATIONS_FILTER_PRIMARY_ONLY, primaryOnly);
        mPrimaryOnly = primaryOnly;
        updateRealmResults();
        setupLocationResults();
    }

    private void setupLocationResults() {
        LocationsAdapter mAdapter = new LocationsAdapter(LocationsActivity.this);
        RealmLocationsListAdapter realmAdapter =
                new RealmLocationsListAdapter(getApplicationContext(), mRealmResults);
        mAdapter.setRealmAdapter(realmAdapter);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.locations_list);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Context context = getApplicationContext();
        PreferencesHelper preferencesHelper = new PreferencesHelper(context);
        preferencesHelper.save(LOCATIONS_FILTER, mLocationsFilter);
        preferencesHelper.save(LOCATIONS_FILTER_ROW, mSelectedRow);
        preferencesHelper.save(LOCATIONS_FILTER_PRIMARY_ONLY, mPrimaryOnly);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferencesHelper preferencesHelper = new PreferencesHelper(this);
        mLocationsFilter = preferencesHelper.loadString(LOCATIONS_FILTER, FREEZER);
        mSelectedRow = preferencesHelper.loadString(LOCATIONS_FILTER_ROW, EMPTY);
        mPrimaryOnly = preferencesHelper.loadBoolean(LOCATIONS_FILTER_PRIMARY_ONLY);
        mSpinner.setSelection(getFilterPosition(mLocationsFilter));
        mPrimaryCheckBox.setChecked(mPrimaryOnly);
        updateRealmResults();
        setupRowResults();
        setupLocationResults();
    }

    /**
     * getFilterPosition
     * Returns the index of the specific filter name
     *
     * @param filter the filename (Freezer, Cooler, Dry, Paper, All)
     * @return the index
     */
    private int getFilterPosition(String filter) {
        int position = 4;
        switch (filter) {
            case FREEZER:
                position = 0;
                break;
            case COOLER:
                position = 1;
                break;
            case DRY:
                position = 2;
                break;
            case PAPER:
                position = 3;
                break;
        }
        return position;
    }
}