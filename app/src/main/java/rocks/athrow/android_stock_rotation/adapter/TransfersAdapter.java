package rocks.athrow.android_stock_rotation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Date;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.Transfer;
import rocks.athrow.android_stock_rotation.realmadapter.RealmRecyclerViewAdapter;
import rocks.athrow.android_stock_rotation.util.Utilities;

/**
 * TransfersAdapter
 * Created by jose on 1/15/17.
 */

public class TransfersAdapter extends RealmRecyclerViewAdapter<Transfer> {
    private final Context mContext;

    public TransfersAdapter(Context context) {
        this.mContext = context;

    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView viewDate;
        TextView viewSku;
        TextView viewItemDescription;
        TextView viewPackSize;
        TextView viewReceivedDate;
        TextView viewType1;
        TextView viewType2;
        TextView viewLocation;
        TextView viewCaseQty;
        TextView viewLooseQty;

        ViewHolder(View view) {
            super(view);
            viewDate = (TextView) view.findViewById(R.id.transfer_date);
            viewSku = (TextView) view.findViewById(R.id.transfer_sku);
            viewItemDescription = (TextView) view.findViewById(R.id.transfer_item_description);
            viewPackSize = (TextView) view.findViewById(R.id.transfer_pack_size);
            viewReceivedDate = (TextView) view.findViewById(R.id.transfer_received_date);
            viewType1 = (TextView) view.findViewById(R.id.transfer_type1);
            viewType2 = (TextView) view.findViewById(R.id.transfer_type2);
            viewLocation = (TextView) view.findViewById(R.id.transfer_location);
            viewCaseQty = (TextView) view.findViewById(R.id.transfer_case_qty);
            viewLooseQty = (TextView) view.findViewById(R.id.transfer_loose_qty);
        }
    }

    @Override
    public TransfersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View transferItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transfer, parent, false);
        return new TransfersAdapter.ViewHolder(transferItem);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        TransfersAdapter.ViewHolder vh = (TransfersAdapter.ViewHolder) viewHolder;
        Transfer transfer = getItem(position);
        Date date = transfer.getDate();
        int sku = transfer.getSku();
        String itemDescription = transfer.getItemDescription();
        String packSize = transfer.getPackSize();
        String receivedDate = transfer.getReceivedDate();
        String type1 = transfer.getTransactionType();
        String type2 = transfer.getType();
        String location = transfer.getLocation();
        int caseQty = transfer.getCaseQty();
        int looseQty = transfer.getLooseQty();
        String dateString = date.toString();
        String skuString = String.valueOf(sku);
        String caseQtyString = String.valueOf(caseQty);
        String looseQtyString = String.valueOf(looseQty);
        vh.viewDate.setText(dateString);
        vh.viewSku.setText(skuString);
        vh.viewItemDescription.setText(itemDescription);
        vh.viewPackSize.setText(packSize);
        vh.viewReceivedDate.setText(receivedDate);
        vh.viewType1.setText(type1);
        vh.viewType2.setText(type2);
        vh.viewLocation.setText(location);
        vh.viewCaseQty.setText(caseQtyString);
        vh.viewLooseQty.setText(looseQtyString);
    }

    @Override
    public int getItemCount() {
        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }
}