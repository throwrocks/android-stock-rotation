package rocks.athrow.android_stock_rotation.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rocks.athrow.android_stock_rotation.api.API;
import rocks.athrow.android_stock_rotation.api.APIResponse;
import rocks.athrow.android_stock_rotation.data.Item;
import rocks.athrow.android_stock_rotation.data.Location;
import rocks.athrow.android_stock_rotation.data.Transfer;
import rocks.athrow.android_stock_rotation.util.PreferencesHelper;
import rocks.athrow.android_stock_rotation.util.Utilities;

/**
 * UpdateDBService
 * Created by joselopez on 1/10/17.
 */

public class SyncDBService extends IntentService {
    public static final String SERVICE_NAME = "SyncDBService";
    public static final String DATA = "data";
    private final static String DATE_TIME_DISPLAY = "MM/dd/yy h:mm:ss a";

    public SyncDBService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Number itemLastSerialNumber = realm.where(Item.class).findAll().max(Item.FIELD_SERIAL_NUMBER);
        int itemSerialNumber = 0;
        if (itemLastSerialNumber != null) {
            itemSerialNumber = itemLastSerialNumber.intValue();
        }
        Number transfersLastSerialNumber = realm.where(Transfer.class).findAll().max(Transfer.FIELD_SERIAL_NUMBER);
        int transfersSerialNumber = 0;
        if (transfersLastSerialNumber != null) {
            transfersSerialNumber = transfersLastSerialNumber.intValue();
        }
        Number locationLastSerialNumber = realm.where(Location.class).findAll().max(Location.FIELD_SERIAL_NUMBER);
        int locationSerialNumber = 0;
        if (locationLastSerialNumber != null) {
            locationSerialNumber = locationLastSerialNumber.intValue();
        }
        realm.commitTransaction();
        realm.close();
        APIResponse itemsResponse = API.getItems(itemSerialNumber);
        APIResponse transfersResponse = API.getTransfers(transfersSerialNumber);
        APIResponse locationsResponse = API.getLocations(locationSerialNumber);
        if (itemsResponse.getResponseCode() == 200) {
            updateDB("items", itemsResponse.getResponseText());
        }
        if (transfersResponse.getResponseCode() == 200) {
            updateDB("transfers", transfersResponse.getResponseText());
        }
        if (locationsResponse.getResponseCode() == 200) {
            updateDB("locations", locationsResponse.getResponseText());
        }
        PreferencesHelper preferencesHelper = new PreferencesHelper(getApplicationContext());
        preferencesHelper.save("last_sync", new Date().toString());
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(SERVICE_NAME));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void updateDB(String type, String responseText) {
        JSONArray jsonArray = getJSONArray(responseText);
        if (jsonArray == null) {
            return;
        }
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        switch (type) {
            case "items":
                int countItems = jsonArray.length();
                for (int i = 0; i < countItems; i++) {
                    try {
                        Item item = new Item();
                        JSONObject record = jsonArray.getJSONObject(i);
                        realm.beginTransaction();
                        item.setId(record.getString(Item.FIELD_ID));
                        item.setSerialNumber(record.getInt(Item.FIELD_SERIAL_NUMBER));
                        item.setTagNumber(record.getString(Item.FIELD_TAG_NUMBER));
                        item.setSKU(record.getInt(Item.FIELD_SKU));
                        item.setDescription(record.getString(Item.FIELD_DESCRIPTION));
                        item.setPackSize(record.getString(Item.FIELD_PACK_SIZE));
                        item.setReceivingId(record.getInt(Item.FIELD_RECEIVING_ID));
                        item.setReceivedDate(record.getString(Item.FIELD_RECEIVED_DATE));
                        item.setItemType(record.getString(Item.FIELD_ITEM_TYPE));
                        realm.copyToRealmOrUpdate(item);
                        realm.commitTransaction();
                    } catch (JSONException e) {
                        realm.cancelTransaction();
                        e.printStackTrace();
                    }
                }
                break;
            case "locations":
                int countLocations = jsonArray.length();
                for (int i = 0; i < countLocations; i++) {
                    try {
                        Location location = new Location();
                        JSONObject record = jsonArray.getJSONObject(i);
                        realm.beginTransaction();
                        location.setSerialNumber(record.getInt(Location.FIELD_SERIAL_NUMBER));
                        location.setBarcode(record.getString(Location.FIELD_BARCODE));
                        location.setLocation(record.getString(Location.FIELD_LOCATION));
                        location.setType(record.getString(Location.FIELD_TYPE));
                        realm.copyToRealmOrUpdate(location);
                        realm.commitTransaction();
                    } catch (JSONException e) {
                        realm.cancelTransaction();
                        e.printStackTrace();
                    }
                }
                break;
            case "transfers":
                int countTransfers = jsonArray.length();
                for (int i = 0; i < countTransfers; i++) {
                    try {
                        Transfer transfer = new Transfer();
                        JSONObject record = jsonArray.getJSONObject(i);
                        realm.beginTransaction();
                        transfer.setId(record.getString(Transfer.FIELD_ID));
                        transfer.setType(record.getString(Transfer.FIELD_TYPE));
                        transfer.setSerialNumber(record.getInt(Transfer.FIELD_SERIAL_NUMBER));
                        transfer.setTransactionId(record.getString(Transfer.FIELD_TRANSACTION_ID));
                        transfer.setTransactionType(record.getString(Transfer.FIELD_TRANSACTION_TYPE));
                        transfer.setDate(Utilities.getStringAsDate(record.getString(Transfer.FIELD_DATE), DATE_TIME_DISPLAY, null));
                        transfer.setItemId(record.getString(Transfer.FIELD_ITEM_ID));
                        transfer.setSku(record.getInt(Transfer.FIELD_SKU));
                        transfer.setItemDescription(record.getString(Transfer.FIELD_ITEM_DESCRIPTION));
                        transfer.setPackSize(record.getString(Transfer.FIELD_PACK_SIZE));
                        transfer.setReceivingId(record.getInt(Transfer.FIELD_RECEIVING_ID));
                        transfer.setReceivedDate(record.getString(Transfer.FIELD_RECEIVED_DATE));
                        transfer.setLocation(record.getString(Transfer.FIELD_LOCATION));
                        transfer.setCaseQty(record.getInt(Transfer.FIELD_CASE_QTY));
                        transfer.setLooseQty(record.getInt(Transfer.FIELD_LOOSE_QTY));
                        realm.copyToRealmOrUpdate(transfer);
                        realm.commitTransaction();
                    } catch (JSONException e) {
                        realm.cancelTransaction();
                        e.printStackTrace();
                    }
                }
                break;
            default:
                realm.close();
        }
    }

    private static JSONArray getJSONArray(String JSON) {
        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(JSON);
            jsonArray = jsonObject.getJSONArray(DATA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}
