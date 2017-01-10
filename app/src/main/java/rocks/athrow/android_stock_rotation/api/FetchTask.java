package rocks.athrow.android_stock_rotation.api;

import android.os.AsyncTask;

import rocks.athrow.android_stock_rotation.interfaces.OnTaskComplete;

/**
 * FetchTask
 * Created by joselopez on 1/10/17.
 */

public class FetchTask extends AsyncTask<String, Void, APIResponse> {
    public static final String ITEMS = "items";
    public static final String LOCATIONS = "locations";
    public static final String TRANSACTIONS = "transactions";
    private OnTaskComplete mListener = null;

    public FetchTask(OnTaskComplete listener) {
        this.mListener = listener;
    }

    @Override
    protected APIResponse doInBackground(String... String) {
        APIResponse apiResponse = new APIResponse();
        String type = String[0];
        String param = String[1];
        int serialNumber = 0;
        if ( param != null ){
            serialNumber = Integer.parseInt(param);
        }

        switch (type) {
            case ITEMS:
                apiResponse = API.getItems(serialNumber);
                apiResponse.setMeta(ITEMS);
                break;
            case LOCATIONS:
                apiResponse = API.getLocations(serialNumber);
                apiResponse.setMeta(LOCATIONS);
                break;
            // TODO: Add Transactions API call
        }
        return apiResponse;
    }

    @Override
    protected void onPostExecute(APIResponse apiResponse) {
        super.onPostExecute(apiResponse);
        mListener.OnTaskComplete(apiResponse);
    }
}
