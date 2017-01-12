package rocks.athrow.android_stock_rotation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.adapter.RotationAdapter;
import rocks.athrow.android_stock_rotation.data.Transaction;
import rocks.athrow.android_stock_rotation.realmadapter.RealmTransactionsListAdapter;

/**
 * RotationActivity
 * Created by joselopez on 1/9/17.
 */

public class RotationActivity extends AppCompatActivity {
    public static final String ADD_ITEM_ACTION = "action";
    public static final String ACTION_SCAN = "scan";
    private String mRotationType;
    private RotationAdapter mRotationAdapter;
    private RealmResults<Transaction> mRealmResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotation);
        Intent intent = getIntent();
        mRotationType = intent.getStringExtra(MainActivity.MODULE_TYPE);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(mRotationType);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(MainActivity.MODULE_TYPE, mRotationType);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mRotationType = savedInstanceState.getString(MainActivity.MODULE_TYPE);
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void updateRealmResults() {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        mRealmResults =
                realm.where(Transaction.class).
                        equalTo(Transaction.TYPE1, mRotationType).
                        equalTo(Transaction.IS_COMPLETE, false).findAll();
        realm.commitTransaction();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRealmResults();
        setupRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_rotation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rotation_add_item:
                Intent intent = new Intent(this, ScanActivity.class);
                intent.putExtra(MainActivity.MODULE_TYPE, mRotationType);
                intent.putExtra(ADD_ITEM_ACTION, ACTION_SCAN);
                intent.putExtra(ScanActivity.MODE, ScanActivity.MODE_EDIT);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupRecyclerView() {
        mRotationAdapter = new RotationAdapter(mRotationType, RotationActivity.this);
        RealmTransactionsListAdapter realmTransactionsListAdapter =
                new RealmTransactionsListAdapter(getApplicationContext(), mRealmResults);
        mRotationAdapter.setRealmAdapter(realmTransactionsListAdapter);
        RecyclerView transactionsList = (RecyclerView) findViewById(R.id.rotation_list);
        transactionsList.setLayoutManager(new LinearLayoutManager(this));
        transactionsList.setAdapter(mRotationAdapter);
    }

}
