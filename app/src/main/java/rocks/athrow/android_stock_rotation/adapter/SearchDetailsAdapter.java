package rocks.athrow.android_stock_rotation.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.UUID;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.activity.TransactionMoveActivity;
import rocks.athrow.android_stock_rotation.activity.TransactionOutActivity;
import rocks.athrow.android_stock_rotation.data.LocationItem;
import rocks.athrow.android_stock_rotation.data.Z;

import static rocks.athrow.android_stock_rotation.data.Z.CURRENT_LOCATION;
import static rocks.athrow.android_stock_rotation.data.Z.MODE;
import static rocks.athrow.android_stock_rotation.data.Z.MODE_EDIT;
import static rocks.athrow.android_stock_rotation.data.Z.MODULE_STAGING;
import static rocks.athrow.android_stock_rotation.data.Z.MODULE_TYPE;
import static rocks.athrow.android_stock_rotation.data.Z.TAG_NUMBER;
import static rocks.athrow.android_stock_rotation.data.Z.TRANSACTION_ID;

/**
 * SearchDetailsAdapter
 * Created by joselopez on 1/27/17.
 */

public class SearchDetailsAdapter extends RecyclerView.Adapter<SearchDetailsAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<LocationItem> mItems;

    public SearchDetailsAdapter(Context context, ArrayList<LocationItem> locationItems) {
        this.mContext = context;
        this.mItems = locationItems;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView skuView;
        TextView itemDescriptionView;
        TextView tagNumberView;
        TextView locationView;
        TextView packSizeView;
        TextView receivedDateView;
        TextView expirationDateView;
        TextView casesView;
        Button adjustButton;
        Button moveButton;
        Button stageButton;
        ViewHolder(View view) {
            super(view);
            skuView = (TextView) view.findViewById(R.id.search_items_item_sku);
            itemDescriptionView = (TextView) view.findViewById(R.id.search_items_item_description);
            tagNumberView = (TextView) view.findViewById(R.id.search_items_tag_number);
            locationView = (TextView) view.findViewById(R.id.search_items_location);
            packSizeView = (TextView) view.findViewById(R.id.search_items_pack_size);
            receivedDateView = (TextView) view.findViewById(R.id.search_items_received_date);
            expirationDateView = (TextView) view.findViewById(R.id.search_items_expirtation_date);
            casesView = (TextView) view.findViewById(R.id.search_items_cases_qty);
            adjustButton = (Button) view.findViewById(R.id.search_items_adjust_button);
            moveButton = (Button) view.findViewById(R.id.search_items_move_button);
            //stageButton = (Button) view.findViewById(R.id.search_items_stage_button);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View locationItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_item, parent, false);
        return new ViewHolder(locationItem);

    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        LocationItem locationItem = mItems.get(position);
        String itemSku = String.valueOf(locationItem.getSKU());
        String itemDescription = locationItem.getDescription();
        final String tagNumber = locationItem.getInventoryTag();
        final String location = locationItem.getLocation();
        String packSize = locationItem.getPackSize();
        String receivedDate = locationItem.getReceivedDate();
        String expirationDate = locationItem.getExpirationDate();
        String countCases = locationItem.getCaseQty();
        viewHolder.skuView.setText(itemSku);
        viewHolder.itemDescriptionView.setText(itemDescription);
        viewHolder.tagNumberView.setText(tagNumber);
        viewHolder.locationView.setText(location);
        viewHolder.packSizeView.setText(packSize);
        viewHolder.receivedDateView.setText(receivedDate);
        viewHolder.expirationDateView.setText(expirationDate);
        viewHolder.casesView.setText(countCases);
        viewHolder.moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = UUID.randomUUID().toString();
                Intent intent = new Intent(mContext, TransactionMoveActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(MODULE_TYPE, Z.MODULE_MOVING);
                intent.putExtra(TRANSACTION_ID, id);
                intent.putExtra(CURRENT_LOCATION, location);
                intent.putExtra(TAG_NUMBER, tagNumber);
                intent.putExtra(MODE, MODE_EDIT);
                mContext.startActivity(intent);
            }
        });
        viewHolder.stageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = UUID.randomUUID().toString();
                Intent intent = new Intent(mContext, TransactionOutActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(MODULE_TYPE, MODULE_STAGING);
                intent.putExtra(TRANSACTION_ID, id);
                intent.putExtra(CURRENT_LOCATION, location);
                intent.putExtra(TAG_NUMBER, tagNumber);
                intent.putExtra(MODE, MODE_EDIT);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if ( mItems != null ){
            return mItems.size();
        }else{
            return 0;
        }
    }
}
