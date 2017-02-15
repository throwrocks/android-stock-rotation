package rocks.athrow.android_stock_rotation.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.adapter.TransfersAdapter;
import rocks.athrow.android_stock_rotation.data.RealmQueries;
import rocks.athrow.android_stock_rotation.data.Transfer;
import rocks.athrow.android_stock_rotation.realmadapter.RealmTransfersListAdapter;

/**
 * TransfersActivity
 * Created by jose on 1/15/17.
 */

public class TransfersActivity extends AppCompatActivity {
    private RealmResults<Transfer> mRealmResults;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfers);
        updateRealmResults();
        setupRecyclerView();
    }

    private void updateRealmResults() {
        Context context = getApplicationContext();
        mRealmResults = RealmQueries.getTransfers(context);
    }

    private void setupRecyclerView() {
        if (mRealmResults != null && mRealmResults.size() > 0) {
            TransfersAdapter mTransferAdapter = new TransfersAdapter(TransfersActivity.this);
            RealmTransfersListAdapter realmTransfersListAdapter =
                    new RealmTransfersListAdapter(getApplicationContext(), mRealmResults);
            mTransferAdapter.setRealmAdapter(realmTransfersListAdapter);
            RecyclerView transfersList = (RecyclerView) findViewById(R.id.transfers_list);
            transfersList.setLayoutManager(new LinearLayoutManager(this));
            transfersList.setAdapter(mTransferAdapter);
        }
    }
}
