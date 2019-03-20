package rocks.athrow.android_stock_rotation.data;

import android.content.Context;
import android.util.Log;

import com.joselopezrosario.androidfm.FmData;
import com.joselopezrosario.androidfm.FmEdit;
import com.joselopezrosario.androidfm.FmRecord;
import com.joselopezrosario.androidfm.FmResponse;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rocks.athrow.android_stock_rotation.api.API;
import rocks.athrow.android_stock_rotation.util.PreferencesHelper;
import rocks.athrow.android_stock_rotation.util.Utilities;

import static android.R.attr.name;


/**
 * SyncDB
 * Created by joselopez on 1/26/17.
 */

public final class SyncDB {
    private final static String DATE_TIME_DISPLAY = "MM/dd/yy hh:mm:ss";
    private static final String LOG_TAG = "SyncDB";

    public static void downloadNewRecords(Context context) {
        Realm.init(context);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().build();
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
        FmResponse itemsResponse = API.getItems(itemSerialNumber);
        FmResponse transfersResponse = API.getTransfers(transfersSerialNumber);
        FmResponse locationsResponse = API.getLocations(locationSerialNumber);
        if (itemsResponse != null && itemsResponse.getHttpCode() == 200) {
            updateDB(context, "items", itemsResponse);
        }
        if (transfersResponse != null && transfersResponse.getHttpCode() == 200) {
            updateDB(context, "transfers", transfersResponse);
        }
        if (locationsResponse != null && locationsResponse.getHttpCode() == 200) {
            updateDB(context, "locations", locationsResponse);
        }
        PreferencesHelper preferencesHelper = new PreferencesHelper(context);
        preferencesHelper.save("last_sync", new Date().toString());
    }

    public static void postTransfers(Context context){
        Realm.init(context);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm.compactRealm(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Transfer> transfers;
        realm.beginTransaction();
        transfers = realm.where(Transfer.class).equalTo(Transfer.FIELD_INIT, false).findAll();
        int size = transfers.size();
        Log.e(LOG_TAG, "Post Transfers: " + "----------------------------");
        Log.e(LOG_TAG, "Count: " + size);
        if ( size > 0){
            for (int i = 0; i < size; i++) {
                Transfer transfer = transfers.get(0);
                FmEdit edit = new FmEdit()
                        .set(Transfer.FIELD_ID,transfer.getId())
                        .set(Transfer.FIELD_TRANSACTION_ID,transfer.getTransactionId())
                        .set(Transfer.FIELD_TRANSACTION_TYPE,transfer.getTransactionType())
                        .set(Transfer.FIELD_DATE, Utilities.getDateAsString(transfer.getDate(), DATE_TIME_DISPLAY, null))
                        .set(Transfer.FIELD_TYPE,transfer.getType())
                        .set(Transfer.FIELD_ITEM_ID,transfer.getItemId())
                        .set(Transfer.FIELD_SKU,transfer.getSku())
                        .set(Transfer.FIELD_ITEM_DESCRIPTION,transfer.getItemDescription())
                        .set(Transfer.FIELD_TAG_NUMBER,transfer.getTagNumber())
                        .set(Transfer.FIELD_PACK_SIZE,transfer.getPackSize())
                        .set(Transfer.FIELD_RECEIVING_ID,transfer.getReceivingId())
                        .set(Transfer.FIELD_RECEIVED_DATE,transfer.getReceivedDate())
                        .set(Transfer.FIELD_EXPIRATION_DATE,transfer.getExpirationDate())
                        .set(Transfer.FIELD_LOCATION,transfer.getLocation())
                        .set(Transfer.FIELD_CASE_QTY,transfer.getCaseQty())
                        .set(Transfer.FIELD_EMPLOYEE_NUMBER,transfer.getEmployeeNumber());
                FmResponse apiResponse = API.postTransfer(edit);
                if ( apiResponse != null && apiResponse.getHttpCode() == 200){
                    transfer.setItemLocationKey();
                    transfer.setInit(true);
                    transfer.setInitDate(new Date());
                    realm.copyToRealmOrUpdate(transfer);
                }
            }
        }
        realm.commitTransaction();
        realm.close();
    }

    private static void updateDB(Context context, String type, FmResponse response) {
        Realm.init(context);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfig);
        Realm.compactRealm(realmConfig);
        Realm realm = Realm.getDefaultInstance();
        switch (type) {
            case "items":
                FmData itemsData = new FmData(response);
                if (itemsData.size() == 0) {
                    return;
                }
                int countItems = itemsData.size();
                Log.e(LOG_TAG, "Items: " + countItems);
                realm.beginTransaction();
                for (int i = 0; i < countItems; i++) {
                        Log.d(LOG_TAG, "Item update: " + i);
                        Item item = new Item();
                        FmRecord record = itemsData.getRecord(i);
                        item.setId(record.getString(Item.FIELD_ID));
                        item.setSerialNumber(record.getInt(Item.FIELD_SERIAL_NUMBER));
                        item.setTagNumber(record.getString(Item.FIELD_TAG_NUMBER));
                        item.setSKU(record.getInt(Item.FIELD_SKU));
                        item.setDescription(record.getString(Item.FIELD_DESCRIPTION));
                        item.setPackSize(record.getString(Item.FIELD_PACK_SIZE));
                        item.setReceivingId(record.getInt(Item.FIELD_RECEIVING_ID));
                        item.setReceivedDate(record.getString(Item.FIELD_RECEIVED_DATE));
                        item.setExpirationDate(record.getString(Item.FIELD_EXPIRATION_DATE));
                        item.setItemType(record.getString(Item.FIELD_ITEM_TYPE));
                        item.setPrimaryLocation(record.getString(Item.FIELD_PRIMARY_LOCATION));
                        realm.copyToRealmOrUpdate(item);
                }
                realm.commitTransaction();
                realm.close();
                Realm.compactRealm(realmConfig);
                break;
            case "locations":
                FmData locationsData = new FmData(response);
                if (locationsData.size() == 0) {
                    return;
                }
                int countLocations = locationsData.size();
                Log.e(LOG_TAG, "Locations: " + countLocations);
                realm.beginTransaction();
                String[] locationNames = new String[countLocations];
                // Save the locations
                for (int i = 0; i < countLocations; i++) {
                        Location location = new Location();
                        FmRecord record = locationsData.getRecord(i);
                        String locationName = record.getString(Location.FIELD_LOCATION);
                        locationNames[i] = locationName;
                        location.setSerialNumber(record.getInt(Location.FIELD_SERIAL_NUMBER));
                        location.setBarcode(record.getString(Location.FIELD_BARCODE));
                        location.setLocation(locationName);
                        location.setType(record.getString(Location.FIELD_TYPE));
                        location.setPrimary(Boolean.getBoolean(record.getString(Location.FIELD_IS_PRIMARY)));
                        location.setRow(record.getString(Location.FIELD_ROW));
                        realm.copyToRealmOrUpdate(location);
                        Log.d(LOG_TAG, "Save Location " + i + ": " + name);
                }
                // Store the location case quantities
                for ( int i = 0; i < countLocations; i++){
                    String locationName = locationNames[i];
                    int qty = Integer.parseInt(RealmQueries.getCountCasesByLocation(context, locationName, null).toString());
                    RealmResults<Location> locations = realm.where(Location.class).equalTo(Location.FIELD_LOCATION, locationName).findAll();
                    if ( locations.size() > 0 ){
                        Location location = realm.where(Location.class).equalTo(Location.FIELD_LOCATION, locationName).findAll().get(0);
                        location.setFmCaseQty(qty);
                        realm.copyToRealmOrUpdate(location);
                        Log.d(LOG_TAG, "Location count " + i + ": " + locationName + " = " + qty);
                    }
                }
                realm.commitTransaction();
                realm.close();
                Realm.compactRealm(realmConfig);
                break;
            case "transfers":
                FmData transfersData = new FmData(response);
                if (transfersData.size() ==0) {
                    return;
                }
                int countTransfers = transfersData.size();
                Log.e(LOG_TAG, "Transfers: " + countTransfers);
                realm.beginTransaction();
                for (int i = 0; i < countTransfers; i++) {
                        Log.e(LOG_TAG, "Transfer update: " + i);
                        Transfer transfer = new Transfer();
                        FmRecord record = transfersData.getRecord(i);
                        transfer.setRecordId(record.getRecordId());
                        transfer.setModId(record.getModId());
                        transfer.setId(record.getString(Transfer.FIELD_ID));
                        transfer.setType(record.getString(Transfer.FIELD_TYPE));
                        transfer.setSerialNumber(record.getInt(Transfer.FIELD_SERIAL_NUMBER));
                        transfer.setTransactionId(record.getString(Transfer.FIELD_TRANSACTION_ID));
                        transfer.setTransactionType(record.getString(Transfer.FIELD_TRANSACTION_TYPE));
                        transfer.setDate(Utilities.getStringAsDate(record.getString(Transfer.FIELD_DATE), DATE_TIME_DISPLAY, null));
                        transfer.setItemId(record.getString(Transfer.FIELD_ITEM_ID));
                        transfer.setSku(record.getInt(Transfer.FIELD_SKU));
                        transfer.setItemDescription(record.getString(Transfer.FIELD_ITEM_DESCRIPTION));
                        transfer.setTagNumber(record.getString(Transfer.FIELD_TAG_NUMBER));
                        transfer.setPackSize(record.getString(Transfer.FIELD_PACK_SIZE));
                        transfer.setReceivingId(record.getInt(Transfer.FIELD_RECEIVING_ID));
                        transfer.setReceivedDate(record.getString(Transfer.FIELD_RECEIVED_DATE));
                        transfer.setExpirationDate(record.getString(Transfer.FIELD_EXPIRATION_DATE));
                        transfer.setLocation(record.getString(Transfer.FIELD_LOCATION));
                        transfer.setCaseQty(record.getInt(Transfer.FIELD_CASE_QTY));
                        transfer.setEmployeeNumber(record.getInt(Transfer.FIELD_EMPLOYEE_NUMBER));
                        transfer.setEmployeeName(record.getString(Transfer.FIELD_EMPLOYEE_NAME));
                        transfer.setInit(true);
                        transfer.setInitDate(new Date());
                        transfer.setItemLocationKey();
                        realm.copyToRealmOrUpdate(transfer);
                }
                realm.commitTransaction();
                realm.close();
                Realm.compactRealm(realmConfig);
                break;
        }
        realm.close();
    }

}