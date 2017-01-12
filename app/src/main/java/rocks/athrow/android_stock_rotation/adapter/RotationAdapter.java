package rocks.athrow.android_stock_rotation.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import rocks.athrow.android_stock_rotation.activity.MainActivity;
import rocks.athrow.android_stock_rotation.activity.ScanActivity;
import rocks.athrow.android_stock_rotation.realmadapter.RealmRecyclerViewAdapter;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.Transaction;

import static rocks.athrow.android_stock_rotation.activity.RotationActivity.ACTION_SCAN;
import static rocks.athrow.android_stock_rotation.activity.RotationActivity.ADD_ITEM_ACTION;

/**
 * RotationAdapter
 * Created by joselopez on 1/11/17.
 */

public class RotationAdapter extends RealmRecyclerViewAdapter<Transaction> {
    private final String mRotationType;
    private final Context mContext;

    public RotationAdapter(String rotationType, Context context) {
        this.mRotationType = rotationType;
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
        final String id = transaction.getId();
        final String itemId = transaction.getItemId();
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
        if ( mRotationType.equals(MainActivity.MODULE_MOVING)){
            vh.viewLocation2.setVisibility(View.VISIBLE);
            vh.viewLocation2.setText(locationEnd);
        }else{
            vh.viewLocation2.setVisibility(View.GONE);
        }
        vh.viewOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ScanActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(MainActivity.MODULE_TYPE, mRotationType);
                intent.putExtra(ScanActivity.TRANSACTION_ID, id);
                intent.putExtra(ScanActivity.ITEM_ID, itemId);
                intent.putExtra(ADD_ITEM_ACTION, ACTION_SCAN);
                intent.putExtra(ScanActivity.MODE, ScanActivity.MODE_VIEW);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }
}
