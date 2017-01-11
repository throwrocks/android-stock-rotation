package rocks.athrow.android_stock_rotation.realmadapter;

import android.content.Context;

import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.data.Transaction;

/**
 * RealmServiceTicketsListAdapter
 * I'm going to need one more convenience class to help create a RealmModelAdapter
 * supporting the RealmObject type I want
 * http://gradlewhy.ghost.io/realm-results-with-recyclerview/
 */
public class RealmTransactionsListAdapter extends RealmModelAdapter<Transaction> {
    public RealmTransactionsListAdapter(Context context, RealmResults<Transaction> realmResults) {
        super(context, realmResults);
    }
}