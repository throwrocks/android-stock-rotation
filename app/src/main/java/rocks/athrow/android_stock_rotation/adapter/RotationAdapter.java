package rocks.athrow.android_stock_rotation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rocks.athrow.android_stock_rotation.realmadapter.RealmRecyclerViewAdapter;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.Transaction;

/**
 * Created by joselopez on 1/11/17.
 */

public class RotationAdapter extends RealmRecyclerViewAdapter<Transaction> {
    private final Context mContext;

    public RotationAdapter(Context context) {
        this.mContext = context;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        // Declare views
        ViewHolder(View view) {
            super(view);
            // Initialize views
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View serviceTicketCardView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(serviceTicketCardView);

    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder vh = (ViewHolder) viewHolder;
        Transaction transaction = getItem(position);
    }

    @Override
    public int getItemCount() {
        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }
}
