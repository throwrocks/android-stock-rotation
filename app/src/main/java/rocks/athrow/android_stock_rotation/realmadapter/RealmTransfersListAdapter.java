package rocks.athrow.android_stock_rotation.realmadapter;

import android.content.Context;

import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.data.Transfer;

/**
 * RealmTransactionsListAdapter
 * I'm going to need one more convenience class to help create a RealmModelAdapter
 * supporting the RealmObject type I want
 * http://gradlewhy.ghost.io/realm-results-with-recyclerview/
 */
public class RealmTransfersListAdapter extends RealmModelAdapter<Transfer> {
    public RealmTransfersListAdapter(Context context, RealmResults<Transfer> realmResults) {
        super(context, realmResults);
    }
}