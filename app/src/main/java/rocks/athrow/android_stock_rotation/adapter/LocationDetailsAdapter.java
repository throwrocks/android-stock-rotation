package rocks.athrow.android_stock_rotation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.LocationItem;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * LocationDetailsAdapter
 * Created by joselopez on 1/27/17.
 */

public class LocationDetailsAdapter extends RecyclerView.Adapter<LocationDetailsAdapter.ViewHolder> {
    private ArrayList<LocationItem> mItems;

    public LocationDetailsAdapter(ArrayList<LocationItem> locationItems) {
        this.mItems = locationItems;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView skuView;
        TextView itemDescriptionView;
        TextView tagNumberView;
        TextView packSizeView;
        TextView receivedDateView;
        TextView casesView;
        ViewHolder(View view) {
            super(view);
            skuView = (TextView) view.findViewById(R.id.location_details_item_sku);
            itemDescriptionView = (TextView) view.findViewById(R.id.location_details_item_description);
            tagNumberView = (TextView) view.findViewById(R.id.location_details_tag_number);
            packSizeView = (TextView) view.findViewById(R.id.location_details_pack_size);
            receivedDateView = (TextView) view.findViewById(R.id.location_details_received_date);
            casesView = (TextView) view.findViewById(R.id.location_details_cases_qty);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View locationItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_location_details_item, parent, false);
        return new LocationDetailsAdapter.ViewHolder(locationItem);

    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        LocationItem locationItem = mItems.get(position);
        String itemSku = String.valueOf(locationItem.getSKU());
        String itemDescription = locationItem.getDescription();
        String tagNumber = locationItem.getInventoryTag();
        String packSize = locationItem.getPackSize();
        String receivedDate = locationItem.getReceivedDate();
        String countCases = locationItem.getCaseQty();
        viewHolder.skuView.setText(itemSku);
        viewHolder.itemDescriptionView.setText(itemDescription);
        viewHolder.tagNumberView.setText(tagNumber);
        viewHolder.packSizeView.setText(packSize);
        viewHolder.receivedDateView.setText(receivedDate);
        viewHolder.casesView.setText(countCases);
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
