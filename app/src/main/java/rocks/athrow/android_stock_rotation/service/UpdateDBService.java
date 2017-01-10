package rocks.athrow.android_stock_rotation.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.api.FetchTask;
import rocks.athrow.android_stock_rotation.data.Item;
import rocks.athrow.android_stock_rotation.data.Request;

/**
 * UpdateDBService
 * Created by joselopez on 1/10/17.
 */

public class UpdateDBService extends IntentService {
    private static final String SERVICE_NAME = "UpdateDBService";
    public static final String UPDATE_ITEMS_DB_SERVICE_BROADCAST = "UpdateItemsBroadcast";
    public static final String UPDATE_LOCATIONS_DB_SERVICE_BROADCAST = "UpdateLocationsBroadcast";
    public static final String UPDATE_TRANSACTIONS_DB_SERVICE_BROADCAST = "UpdateTransactionsBroadcast";
    public static final String REQUEST_ID = "requestId";
    public static final String TYPE = "type";
    public static final String DATA = "JSON";

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
                int count = jsonArray.length();
                for (int i = 0; i < count; i++) {
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
                break;
            case FetchTask.LOCATIONS:
                break;
            case FetchTask.TRANSACTIONS:
                break;
            default:
                realm.close();
        }
    }

    public static JSONArray getJSONArray(String JSON) {
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
