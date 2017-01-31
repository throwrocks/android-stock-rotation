package rocks.athrow.android_stock_rotation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.LocationItem;

/**
 * Created by joselopez on 1/27/17.
 */

public class SearchDetailsAdapter extends RecyclerView.Adapter<SearchDetailsAdapter.ViewHolder> {
    private ArrayList<LocationItem> mItems;

    public SearchDetailsAdapter(ArrayList<LocationItem> locationItems) {
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
        String tagNumber = locationItem.getInventoryTag();
        String location = locationItem.getLocation();
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
