package rocks.athrow.android_stock_rotation.api;

import com.joselopezrosario.androidfm.Fm;
import com.joselopezrosario.androidfm.FmEdit;
import com.joselopezrosario.androidfm.FmFind;
import com.joselopezrosario.androidfm.FmRequest;
import com.joselopezrosario.androidfm.FmResponse;
import com.joselopezrosario.androidfm.FmScript;

import rocks.athrow.android_stock_rotation.BuildConfig;
import rocks.athrow.android_stock_rotation.data.Item;
import rocks.athrow.android_stock_rotation.data.Location;
import rocks.athrow.android_stock_rotation.data.Transfer;

/**
 * Created by joselopez on 1/9/17.
 */

public final class API {
    private static final String URL = BuildConfig.API_HOST;
    private static final String ACCOUNT = BuildConfig.API_ACCOUNT;
    private static final String PASSWORD = BuildConfig.API_PASSWORD;
    private static final String LAYOUT_LOCATIONS = "locations";
    private static final String LAYOUT_TRANSFERS = "transfers";
    private static final String LAYOUT_ITEMS = "items";
    private static final String LAYOUT_API_KEYS = "api_keys";

    private API() {
        throw new AssertionError("No API instances for you!");
    }

    private static String login() {
        FmResponse login = Fm.execute(
                new FmRequest().login(URL, ACCOUNT, PASSWORD).build()
        );
        if (!login.isOk()) {
            return null;
        }
        return login.getToken();
    }

    private static boolean logout(String token) {
        return Fm.execute(new FmRequest().logout(URL, token).build()).isOk();
    }

    /**
     * getItems
     *
     * @param lastSerialNumber the last serial number in the database. Used so the query only
     *                         returns new items.
     * @return an LocalResponse object with the results
     */
    public static FmResponse getItems(int lastSerialNumber) {
        String token = login();
        if (token == null) {
            return null;
        }
        String sc;
        if (lastSerialNumber == 0) {
            sc = "*";
        } else {
            sc = ">" + lastSerialNumber;
        }
        FmFind find = new FmFind().newRequest().set(Item.FIELD_SERIAL_NUMBER, sc);
        FmRequest request = new FmRequest()
                .findRecords(URL, token, LAYOUT_ITEMS, find)
                .setLimit(5000)
                .build();
        FmResponse response = Fm.execute(request);
        logout(token);
        return response;
    }

    /**
     * getLocations
     *
     * @param lastSerialNumber the last serial number in the database. Used so the query only
     *                         returns new items.
     * @return an LocalResponse object with the results
     */
    public static FmResponse getLocations(int lastSerialNumber) {
        String token = login();
        if (token == null) {
            return null;
        }
        String sc;
        if (lastSerialNumber == 0) {
            sc = "*";
        } else {
            sc = ">" + lastSerialNumber;
        }
        FmFind find = new FmFind().newRequest().set(Location.FIELD_SERIAL_NUMBER, sc);
        FmRequest request = new FmRequest()
                .findRecords(URL, token, LAYOUT_LOCATIONS, find)
                .setLimit(2000)
                .build();
        FmResponse response = Fm.execute(request);
        logout(token);
        return response;
    }

    /**
     * getTransfers
     *
     * @param lastSerialNumber the last serial number in the database. Used so the query only
     *                         returns new items.
     * @return an FmResponse object with the results
     */
    public static FmResponse getTransfers(int lastSerialNumber) {
        String token = login();
        if (token == null) {
            return null;
        }
        String sc;
        if (lastSerialNumber == 0) {
            sc = "*";
        } else {
            sc = ">" + lastSerialNumber;
        }
        FmFind find = new FmFind().newRequest().set(Transfer.FIELD_SERIAL_NUMBER, sc);
        FmRequest request = new FmRequest()
                .findRecords(URL, token, LAYOUT_TRANSFERS, find)
                .setLimit(5000)
                .build();
        FmResponse response = Fm.execute(request);
        logout(token);
        return response;
    }

    public static FmResponse getItemBySKU(int sku) {
        String token = login();
        if (token == null) {
            return null;
        }
        FmFind find = new FmFind().newRequest().set(Item.FIELD_SKU, "==" + sku);
        FmRequest request = new FmRequest().findRecords(URL, token, LAYOUT_ITEMS, find).build();
        FmResponse response = Fm.execute(request);
        logout(token);
        return response;
    }

    public static FmResponse getItemByTag(String tag) {
        String token = login();
        if (token == null) {
            return null;
        }
        FmFind find = new FmFind().newRequest().set(Item.FIELD_TAG_NUMBER, "==" + tag);
        FmRequest request = new FmRequest().findRecords(URL, token, LAYOUT_ITEMS, find).build();
        FmResponse response = Fm.execute(request);
        logout(token);
        return response;
    }

    public static FmResponse postTransfer(FmEdit edit) {
        String token = login();
        if (token == null) {
            return null;
        }
        FmRequest request = new FmRequest().create(URL, token, LAYOUT_TRANSFERS, edit).build();
        FmResponse response = Fm.execute(request);
        logout(token);
        return response;
    }

    public static FmResponse initTransfers() {
        String token = login();
        if (token == null) {
            return null;
        }
        FmScript script = new FmScript().setScript("api_init_item_locations");
        FmRequest request = new FmRequest()
                .getRecords(URL, token, LAYOUT_ITEMS)
                .setLimit(1)
                .setScriptParams(script)
                .build();
        FmResponse response = Fm.execute(request);
        logout(token);
        return response;
    }

    public static FmResponse validateKey(int key) {
        String token = login();
        if (token == null) {
            return null;
        }
        FmScript script = new FmScript().setScript("api_validate_key").setScriptParam(String.valueOf(key));
        FmRequest request = new FmRequest()
                .getRecords(URL, token, LAYOUT_API_KEYS)
                .setLimit(1)
                .setScriptParams(script)
                .build();
        FmResponse response = Fm.execute(request);
        logout(token);
        return response;
    }
}
