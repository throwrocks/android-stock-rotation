package rocks.athrow.android_stock_rotation.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.adapter.LocationsAdapter;
import rocks.athrow.android_stock_rotation.data.RealmQueries;
import rocks.athrow.android_stock_rotation.data.Location;
import rocks.athrow.android_stock_rotation.realmadapter.RealmLocationsListAdapter;
import rocks.athrow.android_stock_rotation.util.PreferencesHelper;

import static android.R.attr.type;


/**
 * LocationsActivity
 * Created by jose on 1/15/17.
 */

public class LocationsActivity extends AppCompatActivity {
    private final static String LOCATIONS_FILTER = "locations_filter";
    private final static String LOCATIONS_SEARCH_CRITERIA = "locations_search_criteria";
    private final static String LOCATIONS_FILTER_PRIMARY_ONLY = "locations_filter_primary_only";
    private final static String EMPTY = "";
    private final static String ALL = "All";
    private final static String FREEZER = "Freezer";
    private final static String COOLER = "Cooler";
    private final static String PAPER = "Paper";
    private final static String DRY = "Dry";
    private final static CharSequence[] SEARCH_FILTERS = {FREEZER, COOLER, DRY, PAPER, ALL};
    private String mLocationsFilter;
    private String mSearchCriteria;
    private CheckBox mPrimaryCheckBox;
    private boolean mPrimaryOnly;
    private LocationsAdapter mAdapter;
    private RealmResults<Location> mRealmResults;
    private EditText mSearchField;
    private Spinner mSpinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getApplicationContext();
        setContentView(R.layout.activity_locations);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mSearchField = (EditText) findViewById(R.id.locations_search);
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
        /** Search Field Click Listener  **/
        mSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mSearchCriteria = mSearchField.getText().toString();
                    PreferencesHelper preferencesHelper = new PreferencesHelper(context);
                    preferencesHelper.save(LOCATIONS_SEARCH_CRITERIA, mSearchCriteria);
                    mRealmResults = RealmQueries.getLocations(getApplicationContext(), mLocationsFilter, mSearchCriteria, mPrimaryOnly);
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
    }

    private void updateRealmResults() {
        Context context = getApplicationContext();
        if (mSearchCriteria != null && !mSearchCriteria.isEmpty()) {
            mRealmResults = RealmQueries.getLocations(context, mLocationsFilter, mSearchCriteria, mPrimaryOnly);
        } else {
            mRealmResults = RealmQueries.getLocations(context, mLocationsFilter, mPrimaryOnly);
        }

    }

    private void filterLocations(String type) {
        Context context = getApplicationContext();
        PreferencesHelper preferencesHelper = new PreferencesHelper(context);
        preferencesHelper.save(LOCATIONS_FILTER, type);
        mLocationsFilter = type;
        updateRealmResults();
        setupRecyclerView();
    }

    private void setPrimaryOnly(boolean primaryOnly) {
        Context context = getApplicationContext();
        PreferencesHelper preferencesHelper = new PreferencesHelper(context);
        preferencesHelper.save(LOCATIONS_FILTER_PRIMARY_ONLY, primaryOnly);
        mPrimaryOnly = primaryOnly;
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Context context = getApplicationContext();
        PreferencesHelper preferencesHelper = new PreferencesHelper(context);
        preferencesHelper.save(LOCATIONS_FILTER, mLocationsFilter);
        preferencesHelper.save(LOCATIONS_SEARCH_CRITERIA, mSearchCriteria);
        preferencesHelper.save(LOCATIONS_FILTER_PRIMARY_ONLY, mPrimaryOnly);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferencesHelper preferencesHelper = new PreferencesHelper(this);
        mLocationsFilter = preferencesHelper.loadString(LOCATIONS_FILTER, ALL);
        mSearchCriteria = preferencesHelper.loadString(LOCATIONS_SEARCH_CRITERIA, EMPTY);
        mPrimaryOnly = preferencesHelper.loadBoolean(LOCATIONS_FILTER_PRIMARY_ONLY);
        mSearchField.setText(mSearchCriteria);
        mSpinner.setSelection(getFilterPosition(mLocationsFilter));
        mPrimaryCheckBox.setChecked(mPrimaryOnly);
        updateRealmResults();
        setupRecyclerView();
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
            case ALL:
                position = 4;
                break;
        }
        return position;
    }
}