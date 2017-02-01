package rocks.athrow.android_stock_rotation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.UUID;

import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.adapter.RotationAdapter;
import rocks.athrow.android_stock_rotation.data.RealmQueries;
import rocks.athrow.android_stock_rotation.data.Transaction;
import rocks.athrow.android_stock_rotation.realmadapter.RealmTransactionsListAdapter;

/**
 * RotationActivity
 * Created by joselopez on 1/9/17.
 */

public class RotationActivity extends AppCompatActivity {
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
        Context context = getApplicationContext();
        mRealmResults = RealmQueries.getPendingTransactions(context, mRotationType);
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
                switch (mRotationType) {
                    case MainActivity.MODULE_MOVING:
                        String moveTransactionId = UUID.randomUUID().toString();
                        Intent moveIntent = new Intent(this, TransactionMoveActivity.class);
                        moveIntent.putExtra("transaction_id", moveTransactionId);
                        moveIntent.putExtra("type", mRotationType);
                        moveIntent.putExtra("mode", "edit");
                        startActivity(moveIntent);
                        break;
                    case MainActivity.MODULE_PICKING:
                        String pickTransactionId = UUID.randomUUID().toString();
                        Intent pickIntent = new Intent(this, TransactionOutActivity.class);
                        pickIntent.putExtra("transaction_id", pickTransactionId);
                        pickIntent.putExtra("type", mRotationType);
                        pickIntent.putExtra("mode", "edit");
                        startActivity(pickIntent);
                        break;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupRecyclerView() {
        TextView emptyText = (TextView) findViewById(R.id.empty_view);
        if (mRealmResults == null || mRealmResults.size() == 0) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
            mRotationAdapter = new RotationAdapter(mRotationType, RotationActivity.this);
            RealmTransactionsListAdapter realmTransactionsListAdapter =
                    new RealmTransactionsListAdapter(getApplicationContext(), mRealmResults);
            mRotationAdapter.setRealmAdapter(realmTransactionsListAdapter);
            RecyclerView transactionsList = (RecyclerView) findViewById(R.id.rotation_list);
            transactionsList.setLayoutManager(new LinearLayoutManager(this));
            transactionsList.setAdapter(mRotationAdapter);
        }
    }
}
