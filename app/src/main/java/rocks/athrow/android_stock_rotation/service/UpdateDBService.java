package rocks.athrow.android_stock_rotation.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.api.FetchTask;
import rocks.athrow.android_stock_rotation.data.Item;
import rocks.athrow.android_stock_rotation.data.Location;
import rocks.athrow.android_stock_rotation.data.Request;
import rocks.athrow.android_stock_rotation.data.Transfer;
import rocks.athrow.android_stock_rotation.util.Utilities;

/**
 * UpdateDBService
 * Created by joselopez on 1/10/17.
 */

public class UpdateDBService extends IntentService {
    private static final String SERVICE_NAME = "UpdateDBService";
    public static final String UPDATE_ITEMS_DB_SERVICE_BROADCAST = "UpdateItemsBroadcast";
    public static final String UPDATE_LOCATIONS_DB_SERVICE_BROADCAST = "UpdateLocationsBroadcast";
    public static final String UPDATE_TRANSFERS_DB_SERVICE_BROADCAST = "UpdateTransfersBroadcast";
    public static final String REQUEST_ID = "requestId";
    public static final String TYPE = "type";
    public static final String DATA = "JSON";
    private final static String DATE_TIME_DISPLAY = "MM/dd/yy h:mm:ss a";
    public UpdateDBService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        Bundle arguments = workIntent.getExtras();
        String id = arguments.getString(REQUEST_ID);
        String type = arguments.getString(TYPE);
        if (type == null) {
            return;
        }
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Request> results = realm.where(Request.class).equalTo(Request.ID, id).findAll();
        String responseText = null;
        if (results.size() == 1) {
            responseText = results.get(0).getAPIResponseText();
        }
        realm.commitTransaction();
        JSONArray jsonArray = getJSONArray(responseText);
        switch (type) {
            case FetchTask.ITEMS:
                int countItems = jsonArray.length();
                for (int i = 0; i < countItems; i++) {
                    realm.beginTransaction();
                    try {
                        Item item = new Item();
                        JSONObject record = jsonArray.getJSONObject(i);
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
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(UPDATE_ITEMS_DB_SERVICE_BROADCAST));
                break;
            case FetchTask.LOCATIONS:
                int countLocations = jsonArray.length();
                for (int i = 0; i < countLocations; i++) {
                    realm.beginTransaction();
                    try {
                        Location location = new Location();
                        JSONObject record = jsonArray.getJSONObject(i);
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
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(UPDATE_LOCATIONS_DB_SERVICE_BROADCAST));
                break;
            case FetchTask.TRANSFERS:
                int countTransfers = jsonArray.length();
                for (int i = 0; i < countTransfers; i++) {
                    realm.beginTransaction();
                    try {
                        Transfer transfer = new Transfer();
                        JSONObject record = jsonArray.getJSONObject(i);
                        transfer.setId(record.getString(Transfer.FIELD_ID));
                        transfer.setType(record.getString(Transfer.FIELD_TYPE));
                        transfer.setSerialNumber(record.getInt(Transfer.FIELD_SERIAL_NUMBER));
                        transfer.setTransactionId(record.getString(Transfer.FIELD_TRANSACTION_ID));
                        transfer.setTransactionType(record.getString(Transfer.FIELD_TRANSACTION_TYPE));
                        transfer.setDate(Utilities.getStringAsDate(record.getString(Transfer.FIELD_DATE),DATE_TIME_DISPLAY ,null));
                        transfer.setItemId(record.getString(Transfer.FIELD_ITEM_ID));
                        transfer.setSku(record.getInt(Transfer.FIELD_SKU));
                        transfer.setItemDescription(record.getString(Transfer.FIELD_ITEM_DESCRIPTION));
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
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(UPDATE_TRANSFERS_DB_SERVICE_BROADCAST));
                break;
            default:
                realm.close();
        }

    }

    private static JSONArray getJSONArray(String JSON) {
        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(JSON);
            jsonArray = jsonObject.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}
