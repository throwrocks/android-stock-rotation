package rocks.athrow.android_stock_rotation.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.activity.LocationDetailActivity;
import rocks.athrow.android_stock_rotation.data.Location;
import rocks.athrow.android_stock_rotation.data.Z;
import rocks.athrow.android_stock_rotation.realmadapter.RealmRecyclerViewAdapter;

/**
 * LocationsAdapter
 * Created by jose on 1/15/17.
 */

public class LocationsAdapter  extends RealmRecyclerViewAdapter<Location> {
    private final Context mContext;

    public LocationsAdapter(Context context) {
        this.mContext = context;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        final CardView viewCard;
        final TextView viewLocationName;
        final TextView viewLocationQty;

        ViewHolder(View view) {
            super(view);
            viewCard = (CardView) view.findViewById(R.id.location_card);
            viewLocationName = (TextView) view.findViewById(R.id.location_name);
            viewLocationQty = (TextView) view.findViewById(R.id.location_count);
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
        final String locationName = location.getLocation();
        boolean isPrimary = location.isPrimary();
        String caseQty = String.valueOf(location.getFmCaseQty());
        vh.viewLocationName.setText(locationName);
        vh.viewLocationQty.setText(caseQty);
        vh.viewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LocationDetailActivity.class);
                intent.putExtra(Z.LOCATION, locationName);
                mContext.startActivity(intent);
            }
        });
        if ( !isPrimary ){
            vh.viewLocationName.setTextColor(ContextCompat.getColor(mContext, R.color.secondaryText));
        }
    }

    @Override
    public int getItemCount() {
        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }
}