package rocks.athrow.android_stock_rotation.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import rocks.athrow.android_stock_rotation.activity.TransactionAdjustActivity;
import rocks.athrow.android_stock_rotation.activity.TransactionInActivity;
import rocks.athrow.android_stock_rotation.activity.TransactionMoveActivity;
import rocks.athrow.android_stock_rotation.realmadapter.RealmRecyclerViewAdapter;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.Transaction;

import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_ADJUST;
import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_MOVING;
import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_RECEIVING;

/**
 * RotationAdapter
 * Created by joselopez on 1/11/17.
 */

public class RotationAdapter extends RealmRecyclerViewAdapter<Transaction> {
    private final static String ROTATION_TYPE = "type";
    private final static String TRANSACTION_ID = "transaction_id";
    private final static String ITEM_ID = "item_id";
    private final static String MODE = "mode";
    private final static String VIEW = "view";
    private final String mRotationType;
    private final Context mContext;

    public RotationAdapter(String rotationType, Context context) {
        this.mRotationType = rotationType;
        this.mContext = context;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        final TextView viewSku;
        final TextView viewDescription;
        final TextView viewTagNumber;
        final TextView viewPackSize;
        final TextView viewReceivedDate;
        final TextView viewExpirationDate;
        final TextView viewLocation1;
        final TextView viewLocation2Label;
        final TextView viewLocation2;
        final TextView viewCaseQty;
        final Button viewButtons;

        ViewHolder(View view) {
            super(view);
            viewSku = (TextView) view.findViewById(R.id.transaction_sku);
            viewDescription = (TextView) view.findViewById(R.id.transaction_description);
            viewTagNumber = (TextView) view.findViewById(R.id.transaction_tag_number);
            viewPackSize = (TextView) view.findViewById(R.id.transaction_pack_size);
            viewReceivedDate = (TextView) view.findViewById(R.id.transaction_received_date);
            viewExpirationDate = (TextView) view.findViewById(R.id.transaction_expiration_date);
            viewLocation1 = (TextView) view.findViewById(R.id.transaction_location1);
            viewLocation2Label = (TextView) view.findViewById(R.id.transaction_label_new_location);
            viewLocation2 = (TextView) view.findViewById(R.id.transaction_location2);
            viewCaseQty = (TextView) view.findViewById(R.id.transaction_case_qty);
            viewButtons = (Button) view.findViewById(R.id.transaction_view_button);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View transactionItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction_card, parent, false);
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
        String tagNumber = transaction.getTagNumber();
        String packSize = transaction.getPackSize();
        String receivedDate = transaction.getReceivedDate();
        String expirationDate = transaction.getExpirationDate();
        String locationStart = transaction.getLocationStart();
        String caseQty = transaction.getQtyCasesString();
        String locationEnd = transaction.getLocationEnd();
        vh.viewSku.setText(sku);
        vh.viewDescription.setText(description);
        vh.viewTagNumber.setText(tagNumber);
        vh.viewPackSize.setText(packSize);
        vh.viewReceivedDate.setText(receivedDate);
        vh.viewExpirationDate.setText(expirationDate);
        vh.viewLocation1.setText(locationStart);
        vh.viewCaseQty.setText(caseQty);
        if (mRotationType.equals("Move") ||  mRotationType.equals("Receive")) {
            vh.viewLocation2Label.setVisibility(View.VISIBLE);
            vh.viewLocation2.setVisibility(View.VISIBLE);
            if (locationEnd.isEmpty()) {
                vh.viewLocation2.setTextColor(ContextCompat.getColor(mContext, R.color.warning));
                vh.viewLocation2.setTypeface(null, Typeface.ITALIC);
                vh.viewLocation2.setText(mContext.getResources().getString(R.string.not_set));
            } else {
                vh.viewLocation2.setText(locationEnd);
            }

        } else {
            vh.viewLocation2Label.setVisibility(View.GONE);
            vh.viewLocation2.setVisibility(View.GONE);
        }
        vh.viewButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                switch (mRotationType) {
                    case MODULE_MOVING:
                        intent = new Intent(mContext, TransactionMoveActivity.class);
                        break;
                    case MODULE_RECEIVING:
                        intent = new Intent(mContext, TransactionInActivity.class);
                        break;
                    case MODULE_ADJUST:
                        intent = new Intent(mContext, TransactionAdjustActivity.class);
                        break;
                    /*case MODULE_STAGING:
                        intent = new Intent(mContext, TransactionOutActivity.class);
                        break;*/
                    default:
                        intent = new Intent(mContext, TransactionInActivity.class);
                }
                intent.putExtra(ROTATION_TYPE, mRotationType);
                intent.putExtra(TRANSACTION_ID, id);
                intent.putExtra(ITEM_ID, itemId);
                intent.putExtra(MODE, VIEW);
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
