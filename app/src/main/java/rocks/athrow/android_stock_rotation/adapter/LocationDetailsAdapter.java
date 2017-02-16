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

import rocks.athrow.android_stock_rotation.activity.TransactionAdjustActivity;
import rocks.athrow.android_stock_rotation.data.Constants;
import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.activity.TransactionMoveActivity;
import rocks.athrow.android_stock_rotation.data.LocationItem;

import static rocks.athrow.android_stock_rotation.data.Constants.CURRENT_LOCATION;
import static rocks.athrow.android_stock_rotation.data.Constants.MODE;
import static rocks.athrow.android_stock_rotation.data.Constants.MODE_EDIT;
import static rocks.athrow.android_stock_rotation.data.Constants.MODULE_TYPE;
import static rocks.athrow.android_stock_rotation.data.Constants.TAG_NUMBER;
import static rocks.athrow.android_stock_rotation.data.Constants.TRANSACTION_ID;


/**
 * LocationDetailsAdapter
 * Created by joselopez on 1/27/17.
 */

public class LocationDetailsAdapter extends RecyclerView.Adapter<LocationDetailsAdapter.ViewHolder> {
    private final Context mContext;
    private final ArrayList<LocationItem> mItems;

    public LocationDetailsAdapter(Context context, ArrayList<LocationItem> locationItems) {
        this.mContext = context;
        this.mItems = locationItems;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView skuView;
        final TextView itemDescriptionView;
        final TextView tagNumberView;
        final TextView packSizeView;
        final TextView receivedDateView;
        final TextView expirationDateView;
        final TextView casesView;
        final Button adjustButton;
        final Button moveButton;
        ViewHolder(View view) {
            super(view);
            skuView = (TextView) view.findViewById(R.id.location_details_item_sku);
            itemDescriptionView = (TextView) view.findViewById(R.id.location_details_item_description);
            tagNumberView = (TextView) view.findViewById(R.id.location_details_tag_number);
            packSizeView = (TextView) view.findViewById(R.id.location_details_pack_size);
            receivedDateView = (TextView) view.findViewById(R.id.location_details_received_date);
            expirationDateView = (TextView) view.findViewById(R.id.location_details_expiration_date);
            casesView = (TextView) view.findViewById(R.id.location_details_cases_qty);
            adjustButton = (Button) view.findViewById(R.id.location_details_button_adjust);
            moveButton = (Button) view.findViewById(R.id.location_details_button_move);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View locationItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location_item, parent, false);
        return new LocationDetailsAdapter.ViewHolder(locationItem);

    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        LocationItem locationItem = mItems.get(position);
        String itemSku = String.valueOf(locationItem.getSKU());
        String itemDescription = locationItem.getDescription();
        final String tagNumber = locationItem.getInventoryTag();
        String packSize = locationItem.getPackSize();
        String receivedDate = locationItem.getReceivedDate();
        String expirationDate = locationItem.getExpirationDate();
        String countCases = locationItem.getCaseQty();
        final String location = locationItem.getLocation();
        viewHolder.skuView.setText(itemSku);
        viewHolder.itemDescriptionView.setText(itemDescription);
        viewHolder.tagNumberView.setText(tagNumber);
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
                intent.putExtra(MODULE_TYPE, Constants.MODULE_MOVING);
                intent.putExtra(TRANSACTION_ID, id);
                intent.putExtra(CURRENT_LOCATION, location);
                intent.putExtra(TAG_NUMBER, tagNumber);
                intent.putExtra(MODE, MODE_EDIT);
                mContext.startActivity(intent);
            }
        });
        viewHolder.adjustButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = UUID.randomUUID().toString();
                Intent intent = new Intent(mContext, TransactionAdjustActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(MODULE_TYPE, Constants.MODULE_ADJUST);
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
