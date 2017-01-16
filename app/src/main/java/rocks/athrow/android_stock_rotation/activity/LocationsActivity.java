package rocks.athrow.android_stock_rotation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.adapter.LocationsAdapter;
import rocks.athrow.android_stock_rotation.data.DataUtilities;
import rocks.athrow.android_stock_rotation.data.Location;
import rocks.athrow.android_stock_rotation.realmadapter.RealmLocationsListAdapter;
import rocks.athrow.android_stock_rotation.util.PreferencesHelper;


/**
 * Created by jose on 1/15/17.
 */

public class LocationsActivity extends AppCompatActivity {
    private final static String LOCATIONS_FILTER = "locations_filter";
    private final static String ALL = "All";
    private final static String FREEZER = "Freezer";
    private final static String COOLER = "Cooler";
    private final static String PAPER = "Paper";
    private final static String DRY = "Dry";
    private final static CharSequence[] SEARCH_FILTERS = {FREEZER, COOLER, DRY, PAPER, ALL};
    private String mLocationsFilter;
    private LocationsAdapter mAdapter;
    private RealmResults<Location> mRealmResults;
    private EditText mSearchField;
    private Spinner mSearchFilter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        PreferencesHelper preferencesHelper = new PreferencesHelper(this);
        mLocationsFilter = preferencesHelper.loadString(LOCATIONS_FILTER, ALL);
        mSearchField = (EditText) findViewById(R.id.locations_search);
        mSearchFilter = (Spinner) findViewById(R.id.locations_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SEARCH_FILTERS);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSearchFilter.setAdapter(spinnerAdapter);
        mSearchFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                filterLocations(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSearchFilter.setSelection(getFilterPosition(mLocationsFilter));
        mSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchCriteria = mSearchField.getText().toString();
                    mRealmResults = DataUtilities.getLocations(getApplicationContext(), mLocationsFilter, searchCriteria);
                    setupRecyclerView();
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
        updateRealmResults();
        setupRecyclerView();
    }

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
            case ALL:
                position = 4;
                break;
        }
        return position;
    }

    private void updateRealmResults() {
        Context context = getApplicationContext();
        if (mLocationsFilter.equals(ALL)) {
            mRealmResults = DataUtilities.getLocations(context);
        } else {
            mRealmResults = DataUtilities.getLocations(context, mLocationsFilter);
        }

    }

    private void filterLocations(String type) {
        mSearchField.setText("");
        Context context = getApplicationContext();
        PreferencesHelper preferencesHelper = new PreferencesHelper(context);
        preferencesHelper.save("locations_filter", type);
        mLocationsFilter = type;
        mRealmResults = DataUtilities.getLocations(context, type);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mAdapter = new LocationsAdapter(LocationsActivity.this);
        RealmLocationsListAdapter realmAdapter =
                new RealmLocationsListAdapter(getApplicationContext(), mRealmResults);
        mAdapter.setRealmAdapter(realmAdapter);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.locations_list);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);
    }
}
