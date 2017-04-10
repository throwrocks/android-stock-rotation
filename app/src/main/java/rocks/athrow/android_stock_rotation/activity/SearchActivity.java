package rocks.athrow.android_stock_rotation.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.adapter.SearchDetailsAdapter;
import rocks.athrow.android_stock_rotation.data.LocationItem;
import rocks.athrow.android_stock_rotation.data.RealmQueries;
import rocks.athrow.android_stock_rotation.util.PreferencesHelper;
import rocks.athrow.android_stock_rotation.util.Utilities;

import static rocks.athrow.android_stock_rotation.data.Constants.EMPTY;
import static rocks.athrow.android_stock_rotation.data.Constants.SEARCH_CRITERIA;

/**
 * SearchActivity
 * Created by jose on 1/15/17.
 */

public class SearchActivity extends AppCompatActivity {
    private String mSearchCriteria;
    private EditText mSearchField;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getApplicationContext();
        setContentView(R.layout.activity_search);
        mSearchField = (EditText) findViewById(R.id.search_items);
        mSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mSearchCriteria = mSearchField.getText().toString();
                    PreferencesHelper preferencesHelper = new PreferencesHelper(context);
                    preferencesHelper.save(SEARCH_CRITERIA, mSearchCriteria);
                    SearchItemsTask searchItemsTask = new SearchItemsTask(getApplicationContext());
                    searchItemsTask.execute(mSearchCriteria);
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
    private void setupItems(ArrayList<LocationItem> items) {
        if (items == null) {
            return;
        }
        int size = items.size();
        if (size > 0) {
            SearchDetailsAdapter mAdapter = new SearchDetailsAdapter(getApplicationContext(), items);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.location_details_container);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(mAdapter);
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveSearchCriteria();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveSearchCriteria();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferencesHelper preferencesHelper = new PreferencesHelper(getApplicationContext());
        mSearchCriteria = preferencesHelper.loadString(SEARCH_CRITERIA, EMPTY);
        if ( !mSearchCriteria.equals(EMPTY)){
            SearchItemsTask searchItemsTask = new SearchItemsTask(getApplicationContext());
            searchItemsTask.execute(mSearchCriteria);
            mSearchField.setText(mSearchCriteria);
        }
    }

    private void saveSearchCriteria() {
        PreferencesHelper preferencesHelper = new PreferencesHelper(getApplicationContext());
        preferencesHelper.save(SEARCH_CRITERIA, mSearchCriteria);
    }

    /**
     * SearchItemsTask
     */
    private class SearchItemsTask extends AsyncTask<String, Void, ArrayList<LocationItem>> {
        final Context context;

        SearchItemsTask(Context context) {
            this.context = context;
        }

        @Override
        protected ArrayList<LocationItem> doInBackground(String... params) {
            return RealmQueries.searchActivityQuery(context, params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<LocationItem> items) {
            super.onPostExecute(items);
            if (items == null) {
                Utilities.showToast(getApplicationContext(),
                        getResources().getString(R.string.item_not_found), Toast.LENGTH_SHORT);
            } else {
                setupItems(items);
            }
        }
    }
}
