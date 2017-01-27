package rocks.athrow.android_stock_rotation.data;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.api.API;
import rocks.athrow.android_stock_rotation.api.APIResponse;
import rocks.athrow.android_stock_rotation.util.PreferencesHelper;
import rocks.athrow.android_stock_rotation.util.Utilities;


/**
 * SyncDB
 * Created by joselopez on 1/26/17.
 */

public final class SyncDB {
    private static final String DATA = "data";
    private final static String DATE_TIME_DISPLAY = "MM/dd/yy h:mm:ss a";
    private static final String LOG_TAG = "SyncDB";

    public static boolean downloadNewRecords(Context context) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Number itemLastSerialNumber = realm.where(Item.class).findAll().max(Item.FIELD_SERIAL_NUMBER);
        Log.e(LOG_TAG, "Last item id: " + itemLastSerialNumber);
        int itemSerialNumber = 0;
        if (itemLastSerialNumber != null) {
            itemSerialNumber = itemLastSerialNumber.intValue();
        }
        Number transfersLastSerialNumber = realm.where(Transfer.class).findAll().max(Transfer.FIELD_SERIAL_NUMBER);
        Log.e(LOG_TAG, "Last transfer id: " + transfersLastSerialNumber);
        int transfersSerialNumber = 0;
        if (transfersLastSerialNumber != null) {
            transfersSerialNumber = transfersLastSerialNumber.intValue();
        }
        Number locationLastSerialNumber = realm.where(Location.class).findAll().max(Location.FIELD_SERIAL_NUMBER);
        Log.e(LOG_TAG, "Last location id: " + locationLastSerialNumber);
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
            updateDB(context, "items", itemsResponse.getResponseText());
        }
        if (transfersResponse.getResponseCode() == 200) {
            updateDB(context, "transfers", transfersResponse.getResponseText());
        }
        if (locationsResponse.getResponseCode() == 200) {
            updateDB(context, "locations", locationsResponse.getResponseText());
        }
        PreferencesHelper preferencesHelper = new PreferencesHelper(context);
        preferencesHelper.save("last_sync", new Date().toString());
        return true;
    }

    public static void storeCalcs(Context context){
        updateDB(context, "update_location_qtys", null);
    }

    private static void updateDB(Context context, String type, String responseText) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm.compactRealm(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        switch (type) {
            case "items":
                JSONArray itemsArray = getJSONArray(responseText);
                if (itemsArray == null) {
                    return;
                }
                int countItems = itemsArray.length();
                Log.e(LOG_TAG, "Items: " + countItems);
                for (int i = 0; i < countItems; i++) {
                    try {
                        Log.d(LOG_TAG, "Item update: " + i);
                        Item item = new Item();
                        JSONObject record = itemsArray.getJSONObject(i);
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
                        item.setEdisonCaseQty(record.getInt(Location.FIELD_EDISON_QTY));
                        realm.copyToRealmOrUpdate(item);
                        realm.commitTransaction();
                    } catch (JSONException e) {
                        realm.cancelTransaction();
                        e.printStackTrace();
                    }
                }
                realm.close();
                break;
            case "locations":
                JSONArray locationsArray = getJSONArray(responseText);
                if (locationsArray == null) {
                    return;
                }
                int countLocations = locationsArray.length();
                Log.e(LOG_TAG, "Locations: " + countLocations);
                for (int i = 0; i < countLocations; i++) {
                    try {
                        Log.d(LOG_TAG, "Location update: " + i);
                        Location location = new Location();
                        JSONObject record = locationsArray.getJSONObject(i);
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
                realm.close();
                break;
            case "transfers":
                JSONArray transfersArray = getJSONArray(responseText);
                if (transfersArray == null) {
                    return;
                }
                int countTransfers = transfersArray.length();
                Log.e(LOG_TAG, "Transfers: " + countTransfers);
                for (int i = 0; i < countTransfers; i++) {
                    try {
                        Log.d(LOG_TAG, "Transfer update: " + i);
                        Transfer transfer = new Transfer();
                        JSONObject record = transfersArray.getJSONObject(i);
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
                realm.close();
                break;
            case "update_location_qtys":
                // TODO: Insead to getting all locations, get locations from all new transfers
                RealmResults<Location> updateLocations = RealmQueries.getLocations(context, "All");
                int countUpdateLocations = updateLocations.size();
                Log.e(LOG_TAG, "Update Location Qtys: " + countUpdateLocations);
                for (int i = 0; i < countUpdateLocations; i++) {
                    Location location = updateLocations.get(i);
                    String name = location.getLocation();
                    int qty = Integer.parseInt(RealmQueries.getCountCasesByLocation(context, name, null).toString());
                    realm.beginTransaction();
                    location.setFmCaseQty(qty);
                    realm.copyToRealmOrUpdate(location);
                    realm.commitTransaction();
                    Log.d(LOG_TAG, "Update Location " + i + ": " + name + " Qty: " + qty);
                }
                realm.close();
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