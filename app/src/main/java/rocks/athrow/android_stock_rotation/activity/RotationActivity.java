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

import static rocks.athrow.android_stock_rotation.data.Constants.MODE;
import static rocks.athrow.android_stock_rotation.data.Constants.MODE_EDIT;
import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_ADJUST;
import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_MOVING;
import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_RECEIVING;
import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_TYPE;
import static rocks.athrow.android_stock_rotation.data.Constants.TRANSACTION_ID;
/**
 * RotationActivity
 * Created by joselopez on 1/9/17.
 */

public class RotationActivity extends AppCompatActivity {
    private String mRotationType;
    private RealmResults<Transaction> mRealmResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotation);
        Intent intent = getIntent();
        mRotationType = intent.getStringExtra(MODULE_TYPE);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(mRotationType);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(MODULE_TYPE, mRotationType);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mRotationType = savedInstanceState.getString(MODULE_TYPE);
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
                    case MODULE_MOVING:
                        String moveTransactionId = UUID.randomUUID().toString();
                        Intent moveIntent = new Intent(this, TransactionMoveActivity.class);
                        moveIntent.putExtra(TRANSACTION_ID, moveTransactionId);
                        moveIntent.putExtra(MODULE_TYPE, mRotationType);
                        moveIntent.putExtra(MODE, MODE_EDIT);
                        startActivity(moveIntent);
                        break;
                    /*case MODULE_STAGING:
                        String pickTransactionId = UUID.randomUUID().toString();
                        Intent pickIntent = new Intent(this, TransactionOutActivity.class);
                        pickIntent.putExtra(TRANSACTION_ID, pickTransactionId);
                        pickIntent.putExtra(MODULE_TYPE, mRotationType);
                        pickIntent.putExtra(MODE, MODE_EDIT);
                        startActivity(pickIntent);
                        break;*/
                    case MODULE_RECEIVING:
                        String receiveTransactionId = UUID.randomUUID().toString();
                        Intent receiveIntent = new Intent(this, TransactionInActivity.class);
                        receiveIntent.putExtra(TRANSACTION_ID, receiveTransactionId);
                        receiveIntent.putExtra(MODULE_TYPE, mRotationType);
                        receiveIntent.putExtra(MODE, MODE_EDIT);
                        startActivity(receiveIntent);
                        break;
                    case MODULE_ADJUST:
                        String adjustTransactionId = UUID.randomUUID().toString();
                        Intent adjustIntent = new Intent(this, TransactionAdjustActivity.class);
                        adjustIntent.putExtra(TRANSACTION_ID, adjustTransactionId);
                        adjustIntent.putExtra(MODULE_TYPE, mRotationType);
                        adjustIntent.putExtra(MODE, MODE_EDIT);
                        startActivity(adjustIntent);
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
            RotationAdapter mRotationAdapter = new RotationAdapter(mRotationType, RotationActivity.this);
            RealmTransactionsListAdapter realmTransactionsListAdapter =
                    new RealmTransactionsListAdapter(getApplicationContext(), mRealmResults);
            mRotationAdapter.setRealmAdapter(realmTransactionsListAdapter);
            RecyclerView transactionsList = (RecyclerView) findViewById(R.id.rotation_list);
            transactionsList.setLayoutManager(new LinearLayoutManager(this));
            transactionsList.setAdapter(mRotationAdapter);
        }
    }
}
