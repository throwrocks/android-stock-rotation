package rocks.athrow.android_stock_rotation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import rocks.athrow.android_stock_rotation.realmadapter.RealmRecyclerViewAdapter;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.Transaction;

/**
 * RotationAdapter
 * Created by joselopez on 1/11/17.
 */

public class RotationAdapter extends RealmRecyclerViewAdapter<Transaction> {
    private final Context mContext;

    public RotationAdapter(Context context) {
        this.mContext = context;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        final TextView viewSku;
        final TextView viewDescription;
        final TextView viewLocation1;
        final TextView viewLocation2;
        final TextView viewCaseQty;
        final TextView viewLooseQty;
        final Button viewOpenButton;
        ViewHolder(View view) {
            super(view);
            viewSku = (TextView) view.findViewById(R.id.transaction_sku);
            viewDescription = (TextView) view.findViewById(R.id.transaction_description);
            viewLocation1 = (TextView) view.findViewById(R.id.transaction_location1);
            viewLocation2 = (TextView) view.findViewById(R.id.transaction_location2);
            viewCaseQty = (TextView) view.findViewById(R.id.transaction_case_qty);
            viewLooseQty = (TextView) view.findViewById(R.id.transaction_loose_qty);
            viewOpenButton = (Button) view.findViewById(R.id.transaction_open_button);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View transactionItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(transactionItem);

    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder vh = (ViewHolder) viewHolder;
        Transaction transaction = getItem(position);
        String sku = transaction.getSkuString();
        String description = transaction.getItemDescription();
        String locationStart = transaction.getLocationStart();
        String caseQty = transaction.getQtyCasesString();
        String looseQty = transaction.getQtyLooseString();
        String locationEnd = transaction.getLocationEnd();
        vh.viewSku.setText(sku);
        vh.viewDescription.setText(description);
        vh.viewLocation1.setText(locationStart);
        vh.viewCaseQty.setText(caseQty);
        vh.viewLooseQty.setText(looseQty);
        vh.viewLocation2.setText(locationEnd);
    }

    @Override
    public int getItemCount() {
        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }
}
