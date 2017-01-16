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
import android.widget.EditText;
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
    private String mLocationsFilter;
    private LocationsAdapter mAdapter;
    private RealmResults<Location> mRealmResults;
    private EditText mSearchField;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        PreferencesHelper preferencesHelper = new PreferencesHelper(this);
        mLocationsFilter = preferencesHelper.loadString(LOCATIONS_FILTER, ALL);
        mSearchField = (EditText) findViewById(R.id.locations_search);
        mSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //mSearchField.setCursorVisible(false);
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

    private void updateRealmResults() {
        Context context = getApplicationContext();
        if (mLocationsFilter.equals(ALL)) {
            mRealmResults = DataUtilities.getLocations(context);
        } else {
            mRealmResults = DataUtilities.getLocations(context, mLocationsFilter);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_location, menu);
        switch (mLocationsFilter) {
            case FREEZER:
                menu.getItem(0).setChecked(true);
                break;
            case COOLER:
                menu.getItem(1).setChecked(true);
                break;
            case DRY:
                menu.getItem(2).setChecked(true);
                break;
            case PAPER:
                menu.getItem(3).setChecked(true);
                break;
            case ALL:
                menu.getItem(4).setChecked(true);
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.location_menu_freezer:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                filterLocations(FREEZER);
                return true;
            case R.id.location_menu_cooler:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                filterLocations(COOLER);
                return true;
            case R.id.location_menu_dry:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                filterLocations(DRY);
                return true;
            case R.id.location_menu_paper:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                filterLocations(PAPER);
                return true;
            case R.id.location_menu_all:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                filterLocations(ALL);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        //TextView emptyText = (TextView) findViewById(R.id.empty_view);
        if (mRealmResults == null || mRealmResults.size() == 0) {
            //emptyText.setVisibility(View.VISIBLE);
        } else {
            //emptyText.setVisibility(View.GONE);
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
}
