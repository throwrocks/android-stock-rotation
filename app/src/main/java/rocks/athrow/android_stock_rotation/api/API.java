package rocks.athrow.android_stock_rotation.api;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import rocks.athrow.android_stock_rotation.BuildConfig;

/**
 * Created by joselopez on 1/9/17.
 */

public final class API {
    private static final String API_HOST = BuildConfig.API_HOST;
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String API_GET_ITEMS = API_HOST + "/script/api_get_items/items.json?RFMkey=" + API_KEY;
    private static final String API_GET_LOCATIONS = API_HOST + "/script/api_get_locations/locations.json?RFMkey=" + API_KEY;

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
        return httpConnect(API_GET_ITEMS + "&RFMscriptParam=" + lastSerialNumber);
    }

    /**
     * getLocations
     * @param lastSerialNumber the last serial number in the database. Used so the query only
     *                         returns new items.
     * @return an APIResponse object with the results
     */
    public static APIResponse getLocations(int lastSerialNumber) {
        return httpConnect(API_GET_LOCATIONS + "&RFMscriptParam=" + lastSerialNumber);
    }

    /**
     * httpConnect
     *
     * @param queryURL the query URL
     * @return an APIResponse object
     */
    private static APIResponse httpConnect(String queryURL) {
        APIResponse apiResponse = new APIResponse();
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            Uri builtUri = Uri.parse(queryURL).buildUpon().build();
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            apiResponse.setResponseCode(urlConnection.getResponseCode());
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
