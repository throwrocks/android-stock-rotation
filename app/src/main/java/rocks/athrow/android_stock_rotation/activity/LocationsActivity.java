package rocks.athrow.android_stock_rotation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.adapter.LocationsAdapter;
import rocks.athrow.android_stock_rotation.data.RealmQueries;
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
    private String mSearchCriteria;
    private LocationsAdapter mAdapter;
    private RealmResults<Location> mRealmResults;
    private EditText mSearchField;
    private Spinner mSearchFilter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getApplicationContext();
        //updateLocationQtys();
        setContentView(R.layout.activity_locations);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        PreferencesHelper preferencesHelper = new PreferencesHelper(this);
        mLocationsFilter = preferencesHelper.loadString(LOCATIONS_FILTER, ALL);
        mSearchCriteria = preferencesHelper.loadString("locations_search_criteria", "");
        mSearchField = (EditText) findViewById(R.id.locations_search);
        mSearchFilter = (Spinner) findViewById(R.id.locations_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SEARCH_FILTERS);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSearchFilter.setAdapter(spinnerAdapter);
        mSearchFilter.setSelection(getFilterPosition(mLocationsFilter));
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
        mSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mSearchCriteria = mSearchField.getText().toString();
                    PreferencesHelper preferencesHelper = new PreferencesHelper(context);
                    preferencesHelper.save("locations_search_criteria", mSearchCriteria);
                    mRealmResults = RealmQueries.getLocations(getApplicationContext(), mLocationsFilter, mSearchCriteria);
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
        mSearchField.setText(mSearchCriteria);
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
        if (mSearchCriteria != null && !mSearchCriteria.isEmpty()) {
            mRealmResults = RealmQueries.getLocations(context, mLocationsFilter, mSearchCriteria);
        } else {
            mRealmResults = RealmQueries.getLocations(context, mLocationsFilter);
        }

    }

    private void filterLocations(String type) {
        Context context = getApplicationContext();
        PreferencesHelper preferencesHelper = new PreferencesHelper(context);
        preferencesHelper.save("locations_filter", type);
        mLocationsFilter = type;
        updateRealmResults();
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

    /** Update the location qty after downloading new transfers, also update when creating transfers
    private void updateLocationQtys() {
        Runnable r = new Runnable() {
            String id = UUID.randomUUID().toString();

            @Override
            public void run() {
                RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
                Realm.setDefaultConfiguration(realmConfig);
                Realm realm = Realm.getDefaultInstance();
                RealmResults<Location> locations = RealmQueries.getLocations(getApplicationContext(), "All");
                if (locations != null && locations.size() > 0) {
                    for (int i = 0; i < locations.size(); i++) {
                        Location location = locations.get(i);
                        String name = location.getLocation();
                        int qty = Integer.parseInt(RealmQueries.getCountCasesByLocation(getApplicationContext(), name, null).toString());
                        realm.beginTransaction();
                        locations.get(i).setCasesQty(qty);
                        realm.commitTransaction();
                        Log.e(id, " " + name);
                    }

                }
                realm.close();
            }
        };
        Thread t = new Thread(r);
        t.start();

    }**/
}