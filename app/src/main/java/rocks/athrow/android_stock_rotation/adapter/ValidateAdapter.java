package rocks.athrow.android_stock_rotation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import rocks.athrow.android_stock_rotation.R;
import rocks.athrow.android_stock_rotation.data.Comparison;
import rocks.athrow.android_stock_rotation.data.Item;
import rocks.athrow.android_stock_rotation.data.Location;
import rocks.athrow.android_stock_rotation.data.LocationItem;
import rocks.athrow.android_stock_rotation.util.Utilities;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * ValidateAdapter
 * Created by jose on 2/11/17.
 */

public class ValidateAdapter extends RecyclerView.Adapter<ValidateAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Comparison> mItems;

    public ValidateAdapter(Context context, ArrayList<Comparison> items) {
        this.mContext = context;
        this.mItems = items;
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout viewContainer;
        LinearLayout viewFmContainer;
        TextView viewTagNumber;
        TextView viewPackSize;
        TextView viewReceivedDate;
        TextView viewExpirationDate;
        TextView viewEdisonTotalQty;
        TextView viewFileMakerTotalQty;
        ViewHolder(View view) {
            super(view);
            viewContainer = (LinearLayout) view.findViewById(R.id.validate_item_container);
            viewFmContainer = (LinearLayout) view.findViewById(R.id.validate_fm_container);
            viewTagNumber = (TextView) view.findViewById(R.id.validate_edison_tag_number);
            viewPackSize = (TextView) view.findViewById(R.id.validate_edison_pack_size);
            viewReceivedDate = (TextView) view.findViewById(R.id.validate_edison_received_date);
            viewExpirationDate = (TextView) view.findViewById(R.id.validate_edison_expiration_date);
            viewEdisonTotalQty = (TextView) view.findViewById(R.id.validate_edison_total_qty);
            viewFileMakerTotalQty = (TextView) view.findViewById(R.id.validate_filemaker_total_qty);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comparison comparison = mItems.get(position);
        JSONObject edisonResult = comparison.getEdisonResult();
        ArrayList<LocationItem> fmResults = comparison.getFmResults();
            try {
                String tagNumber = edisonResult.getString(Item.FIELD_TAG_NUMBER);
                String packSize = edisonResult.getString(Item.FIELD_PACK_SIZE);
                String receivedDate = edisonResult.getString(Item.FIELD_RECEIVED_DATE);
                String expirationDate = edisonResult.getString(Item.FIELD_EXPIRATION_DATE);
                String countCases = edisonResult.getString(Item.FIELD_EDISON_QTY);
                holder.viewTagNumber.setText(tagNumber);
                holder.viewPackSize.setText(packSize);
                holder.viewReceivedDate.setText(receivedDate);
                holder.viewExpirationDate.setText(expirationDate);
                holder.viewEdisonTotalQty.setText(countCases);
                if ( fmResults != null ){
                    int count = fmResults.size();
                    int fmTotalCases = 0;
                    holder.viewFmContainer.removeAllViews();
                    for(int i=0; i < count; i++){
                        LinearLayout fmColumn = getFmColumn(holder);
                        holder.viewFmContainer.addView(fmColumn);
                        LocationItem item = fmResults.get(i);
                        String location = item.getLocation();
                        String qty = item.getCaseQty();
                        fmTotalCases = fmTotalCases + Integer.parseInt(qty);
                        TextView locationView = (TextView) fmColumn.findViewById(R.id.validate_filemaker_location);
                        locationView.setVisibility(View.VISIBLE);
                        TextView qtyView = (TextView) fmColumn.findViewById(R.id.validate_filemaker_case_qty);
                        locationView.setText(location);
                        qtyView.setText(qty);
                    }
                    TextView fmQty = holder.viewFileMakerTotalQty;
                    fmQty.setText(""+fmTotalCases);
                    if ( fmTotalCases == Integer.parseInt(countCases)){
                        Utilities.badgeFormat(fmQty,"match",mContext);
                    }else{
                        Utilities.badgeFormat(fmQty,"mismatch",mContext);
                    }
                }

        }catch (JSONException e) {
                e.printStackTrace();
            }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View locationItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_validate, parent, false);
        return new ViewHolder(locationItem);
    }

    @Override
    public int getItemCount() {
        if ( mItems != null ){
            return mItems.size();
        }else{
            return 0;
        }
    }

    private LinearLayout getFmColumn(ViewHolder holder){
        return (LinearLayout) LayoutInflater.from(holder.viewContainer.getContext())
                        .inflate(R.layout.item_validate_filemaker_column,
                                holder.viewContainer, false);
    }
}
