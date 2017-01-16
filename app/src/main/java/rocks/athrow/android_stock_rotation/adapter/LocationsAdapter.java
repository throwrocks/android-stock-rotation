package rocks.athrow.android_stock_rotation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.Location;
import rocks.athrow.android_stock_rotation.realmadapter.RealmRecyclerViewAdapter;

/**
 * Created by jose on 1/15/17.
 */

public class LocationsAdapter  extends RealmRecyclerViewAdapter<Location> {
    private final Context mContext;

    public LocationsAdapter(Context context) {
        this.mContext = context;

    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView viewLocationName;


        ViewHolder(View view) {
            super(view);
            viewLocationName = (TextView) view.findViewById(R.id.location_name);
        }
    }

    @Override
    public LocationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View locationItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location, parent, false);
        return new LocationsAdapter.ViewHolder(locationItem);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        LocationsAdapter.ViewHolder vh = (LocationsAdapter.ViewHolder) viewHolder;
        Location location = getItem(position);
        String locationName = location.getLocation();
        vh.viewLocationName.setText(locationName);
    }

    @Override
    public int getItemCount() {
        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }
}