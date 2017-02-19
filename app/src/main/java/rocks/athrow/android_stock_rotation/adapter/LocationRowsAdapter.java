package rocks.athrow.android_stock_rotation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.activity.LocationsActivity;
import rocks.athrow.android_stock_rotation.data.LocationRows;

/**
 * LocationRowsAdapter
 * Created by joselopez1 on 2/16/2017.
 */

public class LocationRowsAdapter extends RecyclerView.Adapter<LocationRowsAdapter.ViewHolder> {
    private final Context mContext;
    private final ArrayList<LocationRows> mItems;

    public LocationRowsAdapter(ArrayList<LocationRows> mItems, Context mContext) {
        this.mItems = mItems;
        this.mContext = mContext;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView rowView;

        ViewHolder(View view) {
            super(view);
            rowView = (TextView) view.findViewById(R.id.item_row);
        }
    }

    @Override
    public LocationRowsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View locationItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new LocationRowsAdapter.ViewHolder(locationItem);
    }

    @Override
    public void onBindViewHolder(final LocationRowsAdapter.ViewHolder viewHolder, int position) {
        final LocationRows locationRows = mItems.get(position);
        final int selectedPosition = position;
        final String row = locationRows.getRow();
        final boolean isSelected = locationRows.isSelected();
        TextView rowView = viewHolder.rowView;
        rowView.setText(row);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof LocationsActivity) {
                    ((LocationsActivity) mContext).setRow(row, selectedPosition);
                }
            }
        });
        if ( isSelected){
            rowView.setEnabled(false);
        }else{
            rowView.setEnabled(true);
        }
    }

    @Override
    public int getItemCount() {
        if (mItems != null) {
            return mItems.size();
        } else {
            return 0;
        }
    }

    private void selectRow(LocationRows row, boolean isSelected){
        row.setSelected(isSelected);
    }

}
