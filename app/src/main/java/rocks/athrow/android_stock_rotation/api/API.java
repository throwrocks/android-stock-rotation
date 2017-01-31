package rocks.athrow.android_stock_rotation.api;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import rocks.athrow.android_stock_rotation.BuildConfig;

/**
 * Created by joselopez on 1/9/17.
 */

public final class API {
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String API_HOST = BuildConfig.API_HOST;
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String API_GET_ITEMS = API_HOST + "/script/api_get_items/items.json?RFMkey=" + API_KEY;
    private static final String API_GET_LOCATIONS = API_HOST + "/script/api_get_locations/locations.json?RFMkey=" + API_KEY;
    private static final String API_GET_TRANSFERS = API_HOST + "/script/api_get_transfers/transfers.json?RFMkey=" + API_KEY;
    private static final String API_GET_ITEM_BY_SKU = API_HOST + "/script/api_get_item_by_sku/items.json?RFMkey=" + API_KEY;
    private static final String API_GET_ITEM_BY_TAG = API_HOST + "/script/api_get_item_by_tag/items.json?RFMkey=" + API_KEY;
    private static final String API_UPLOAD_TRANSFER = API_HOST + "/layout/transfers.json?RFMkey=" + API_KEY + "&RFMurlencoded";

    private API() {
        throw new AssertionError("No API instances for you!");
    }

    /**
     * getItems
     * @param lastSerialNumber the last serial number in the database. Used so the query only
     *                         returns new items.
     * @return an APIResponse object with the results
     */
    public static APIResponse getItems(int lastSerialNumber) {
        return httpConnect("GET", API_GET_ITEMS + "&RFMscriptParam=" + lastSerialNumber, null);
    }

    /**
     * getLocations
     * @param lastSerialNumber the last serial number in the database. Used so the query only
     *                         returns new items.
     * @return an APIResponse object with the results
     */
    public static APIResponse getLocations(int lastSerialNumber) {
        return httpConnect("GET", API_GET_LOCATIONS + "&RFMscriptParam=" + lastSerialNumber, null );
    }
    /**
     * getTransfers
     * @param lastSerialNumber the last serial number in the database. Used so the query only
     *                         returns new items.
     * @return an APIResponse object with the results
     */
    public static APIResponse getTransfers(int lastSerialNumber) {
        return httpConnect("GET", API_GET_TRANSFERS + "&RFMscriptParam=" + lastSerialNumber, null);
    }

    public static APIResponse getItemBySKU(int sku){
        return httpConnect("GET", API_GET_ITEM_BY_SKU + "&RFMscriptParam=" + sku, null);
    }

    public static APIResponse getItemByTag(String tag){
        return httpConnect("GET", API_GET_ITEM_BY_TAG + "&RFMscriptParam=" + tag, null);
    }

    public static APIResponse postTransfer(String data){
        return httpConnect("POST", API_UPLOAD_TRANSFER, data);
    }

    /**
     * httpConnect
     *
     * @param queryURL the query URL
     * @return an APIResponse object
     */
    private static APIResponse httpConnect(String requestMethod, String queryURL, String postData) {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setRequestURI(queryURL);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            Uri builtUri = Uri.parse(queryURL).buildUpon().build();
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            switch (requestMethod){
                case "GET":
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    apiResponse.setResponseCode(urlConnection.getResponseCode());
                    break;
                case "POST":
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty( "charset", "utf-8");
                    urlConnection.setRequestProperty( "Content-Type", "application/json");
                    urlConnection.setRequestProperty( "Content-Length", Integer.toString(postData.length()) );
                    urlConnection.setDoOutput(true);
                    byte[] data = postData.getBytes(StandardCharsets.UTF_8);
                    try( DataOutputStream wr = new DataOutputStream( urlConnection.getOutputStream())) {
                        wr.write( data );
                    }
                    apiResponse.setResponseCode(urlConnection.getResponseCode());
                    break;
            }
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                return apiResponse;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            if (buffer.length() == 0) {
                return apiResponse;
            }
            apiResponse.setResponseText(buffer.toString());
        } catch (IOException v) {
            apiResponse.setResponseText(v.toString());
            return apiResponse;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return apiResponse;
    }

}
