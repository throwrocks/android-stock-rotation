package rocks.athrow.android_stock_rotation.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private final static String DATE_TIME_DISPLAY = "MM/dd/yy h:mm a";
    private final Context mContext;

    public TransfersAdapter(Context context) {
        this.mContext = context;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        final TextView viewDate;
        final TextView viewStatus;
        final TextView viewSku;
        final TextView viewItemDescription;
        final TextView viewTagNumber;
        final TextView viewPackSize;
        final TextView viewReceivedDate;
        final TextView viewExpirationDate;
        final TextView viewType1;
        final TextView viewType2;
        final TextView viewLocation;
        final TextView viewCaseQty;
        final TextView viewUser;

        ViewHolder(View view) {
            super(view);
            viewDate = (TextView) view.findViewById(R.id.transfer_date);
            viewStatus = (TextView) view.findViewById(R.id.transfer_status);
            viewSku = (TextView) view.findViewById(R.id.transfer_sku);
            viewItemDescription = (TextView) view.findViewById(R.id.transfer_item_description);
            viewTagNumber = (TextView) view.findViewById(R.id.transfer_tag_number);
            viewPackSize = (TextView) view.findViewById(R.id.transfer_pack_size);
            viewReceivedDate = (TextView) view.findViewById(R.id.transfer_received_date);
            viewExpirationDate = (TextView) view.findViewById(R.id.transfer_expiration_date);
            viewType1 = (TextView) view.findViewById(R.id.transfer_type1);
            viewType2 = (TextView) view.findViewById(R.id.transfer_type2);
            viewLocation = (TextView) view.findViewById(R.id.transfer_location);
            viewCaseQty = (TextView) view.findViewById(R.id.transfer_case_qty);
            viewUser = (TextView) view.findViewById(R.id.transfer_employee);
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
        String WAITING = mContext.getResources().getString(R.string.waiting);
        String POSTED = mContext.getResources().getString(R.string.posted);
        TransfersAdapter.ViewHolder vh = (TransfersAdapter.ViewHolder) viewHolder;
        Transfer transfer = getItem(position);
        Date date = transfer.getDate();
        String dateString = Utilities.getDateAsString(date, DATE_TIME_DISPLAY, null);
        boolean statusBoolean = transfer.getInit();
        String status = WAITING;
        if ( statusBoolean ){
            status = POSTED;
        }
        int sku = transfer.getSku();
        String itemDescription = transfer.getItemDescription();
        String packSize = transfer.getPackSize();
        String tagNumber = transfer.getTagNumber();
        String receivedDate = transfer.getReceivedDate();
        String expirationDate = transfer.getExpirationDate();
        String type1 = transfer.getTransactionType();
        String type2 = transfer.getType();
        String location = transfer.getLocation();
        String user = transfer.getEmployeeName();
        int caseQty = transfer.getCaseQty();
        String skuString = String.valueOf(sku);
        String caseQtyString = String.valueOf(caseQty);
        vh.viewDate.setText(dateString);
        vh.viewStatus.setText(status);
        if ( status.equals(WAITING)){
            vh.viewStatus.setTextColor(ContextCompat.getColor(mContext, R.color.warning));
        }else{
            vh.viewStatus.setTextColor(ContextCompat.getColor(mContext, R.color.primaryText));
        }
        vh.viewSku.setText(skuString);
        vh.viewItemDescription.setText(itemDescription);
        vh.viewTagNumber.setText(tagNumber);
        vh.viewPackSize.setText(packSize);
        vh.viewReceivedDate.setText(receivedDate);
        vh.viewExpirationDate.setText(expirationDate);
        vh.viewType1.setText(type1);
        vh.viewType2.setText(type2);
        Utilities.badgeFormat(vh.viewType1, type1, mContext);
        Utilities.badgeFormat(vh.viewType2, type2, mContext);
        vh.viewLocation.setText(location);
        vh.viewCaseQty.setText(caseQtyString);
        vh.viewUser.setText(user);
    }

    @Override
    public int getItemCount() {
        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }
}
