package rocks.athrow.android_stock_rotation.realmadapter;

/**
 * Created by joselopez on 1/11/17.
 */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * RealmBaseAdapter
 * Provides the inner workings of RealmRecyclerAdapter
 * Requires the implementation of getView() but is not needed
 * http://gradlewhy.ghost.io/realm-results-with-recyclerview/
 */
class RealmModelAdapter<T extends RealmObject> extends RealmBaseAdapter<T> {
    RealmModelAdapter(RealmResults<T> realmResults) {
        super(realmResults);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}