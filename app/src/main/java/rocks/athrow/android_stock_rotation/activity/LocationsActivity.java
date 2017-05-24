package rocks.athrow.android_stock_rotation.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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

import static rocks.athrow.android_stock_rotation.data.Constants.COOLER;
import static rocks.athrow.android_stock_rotation.data.Constants.DRY;
import static rocks.athrow.android_stock_rotation.data.Constants.EMPTY;
import static rocks.athrow.android_stock_rotation.data.Constants.FREEZER;
import static rocks.athrow.android_stock_rotation.data.Constants.LOCATIONS_FILTER;
import static rocks.athrow.android_stock_rotation.data.Constants.LOCATIONS_FILTER_PRIMARY_ONLY;
import static rocks.athrow.android_stock_rotation.data.Constants.LOCATIONS_FILTER_ROW;
import static rocks.athrow.android_stock_rotation.data.Constants.LOCATIONS_FILTER_ROW_INDEX;
import static rocks.athrow.android_stock_rotation.data.Constants.PAPER;


/**
 * LocationsActivity
 * Created by jose on 1/15/17.
 */

public class LocationsActivity extends AppCompatActivity {
    private final static CharSequence[] SEARCH_FILTERS = {FREEZER, COOLER, DRY, PAPER};
    private String mLocationsFilter;
    private CheckBox mPrimaryCheckBox;
    private boolean mPrimaryOnly;
    private RealmResults<Location> mRealmResults;
    private ArrayList<LocationRows> mRows;
    private int mSelectedLocationRowIndex;
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

    private void queryLocations() {
        Context context = getApplicationContext();
        mRealmResults = RealmQueries.getLocations(context, mLocationsFilter, mSelectedRow, mPrimaryOnly);
    }

    private void queryRows() {
        mRows = RealmQueries.getRows(getApplicationContext(), mLocationsFilter);
    }

    private void filterLocations(String type) {
        mLocationsFilter = type;
        queryRows();
        mSelectedLocationRowIndex = 0;
        if (mRows != null) {
            mSelectedRow = mRows.get(mSelectedLocationRowIndex).getRow();
            saveState();
            setRow(mSelectedRow, mSelectedLocationRowIndex);
        }
    }

    public void setRow(String row, int selectedPosition) {
        mSelectedRow = row;
        mSelectedLocationRowIndex = selectedPosition;
        if (mRows != null) {
            clearRowSelections();
            mRows.get(mSelectedLocationRowIndex).setSelected(true);
        }
        setupRowsRecyclerView();
        queryLocations();
        setupLocationsRecyclerView();

        if (mRowAdapter != null) {
            mRowAdapter.notifyDataSetChanged();
        }
    }

    private void clearRowSelections() {
        if (mRows == null) {
            return;
        }
        int count = mRows.size();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                mRows.get(i).setSelected(false);
            }
        }
    }


    private void setPrimaryOnly(boolean primaryOnly) {
        Context context = getApplicationContext();
        PreferencesHelper preferencesHelper = new PreferencesHelper(context);
        preferencesHelper.save(LOCATIONS_FILTER_PRIMARY_ONLY, primaryOnly);
        mPrimaryOnly = primaryOnly;
        queryLocations();
        setupLocationsRecyclerView();
    }

    private void setupRowsRecyclerView() {
        mRowAdapter = new LocationRowsAdapter(mRows, LocationsActivity.this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.location_rows);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mRowAdapter);
    }

    private void setupLocationsRecyclerView() {
        LocationsAdapter mAdapter = new LocationsAdapter(LocationsActivity.this);
        RealmLocationsListAdapter realmAdapter =
                new RealmLocationsListAdapter(getApplicationContext(), mRealmResults);
        mAdapter.setRealmAdapter(realmAdapter);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.locations_list);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        String x = height + " " + width;
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveState();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        saveState();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferencesHelper preferencesHelper = new PreferencesHelper(this);
        mLocationsFilter = preferencesHelper.loadString(LOCATIONS_FILTER, FREEZER);
        mSelectedRow = preferencesHelper.loadString(LOCATIONS_FILTER_ROW, EMPTY);
        mPrimaryOnly = preferencesHelper.loadBoolean(LOCATIONS_FILTER_PRIMARY_ONLY);
        mSelectedLocationRowIndex = preferencesHelper.loadInt(LOCATIONS_FILTER_ROW_INDEX);
        mSpinner.setSelection(getFilterPosition(mLocationsFilter));
        mPrimaryCheckBox.setChecked(mPrimaryOnly);
        queryRows();
        setRow(mSelectedRow, mSelectedLocationRowIndex);
    }

    private void saveState() {
        Context context = getApplicationContext();
        PreferencesHelper preferencesHelper = new PreferencesHelper(context);
        preferencesHelper.save(LOCATIONS_FILTER, mLocationsFilter);
        preferencesHelper.save(LOCATIONS_FILTER_ROW, mSelectedRow);
        preferencesHelper.save(LOCATIONS_FILTER_PRIMARY_ONLY, mPrimaryOnly);
        preferencesHelper.save(LOCATIONS_FILTER_ROW_INDEX, mSelectedLocationRowIndex);
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